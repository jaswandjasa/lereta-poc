package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.ZoneImportResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZoneImportService {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    /**
     * All-or-nothing import (Correction 3): if any zone fails, the entire file is rolled back.
     */
    @Transactional
    public ZoneImportResult importGeoJson(MultipartFile file) {
        log.info("Importing GeoJSON file: {}, size={} bytes", file.getOriginalFilename(), file.getSize());

        List<String> errors = new ArrayList<>();
        int imported = 0;
        String versionTag = "IMPORT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        LocalDateTime importedAt = LocalDateTime.now();

        try {
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            JsonNode root = objectMapper.readTree(content);

            String type = root.path("type").asText();
            JsonNode features;

            if ("FeatureCollection".equals(type)) {
                features = root.path("features");
            } else if ("Feature".equals(type)) {
                features = objectMapper.createArrayNode().add(root);
            } else {
                throw new IllegalArgumentException("GeoJSON must be a Feature or FeatureCollection, got: " + type);
            }

            for (JsonNode feature : features) {
                String zoneName = extractZoneName(feature);
                String riskLevel = extractRiskLevel(feature);

                JsonNode geometry = feature.path("geometry");
                String geomType = geometry.path("type").asText();

                if (!"Polygon".equals(geomType)) {
                    throw new IllegalArgumentException("Only Polygon geometry supported, got: " + geomType + " for zone: " + zoneName);
                }

                // Check duplicate zone name
                Integer existingCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM flood_zones WHERE zone_name = ?",
                        Integer.class, zoneName);
                if (existingCount != null && existingCount > 0) {
                    throw new IllegalArgumentException("Duplicate zone name: " + zoneName);
                }

                JsonNode coordinates = geometry.path("coordinates").get(0);
                String ordinateArray = buildOrdinateArray(coordinates);
                String geojsonStr = objectMapper.writeValueAsString(feature);

                String sql = String.format(
                        "INSERT INTO flood_zones (id, zone_name, risk_level, geom, geojson) VALUES (" +
                        "flood_zones_seq.NEXTVAL, ?, ?, " +
                        "SDO_GEOMETRY(2003, 4326, NULL, SDO_ELEM_INFO_ARRAY(1, 1003, 1), SDO_ORDINATE_ARRAY(%s)), " +
                        "?)", ordinateArray);

                jdbcTemplate.update(sql, zoneName, riskLevel, geojsonStr);

                // Apply version tag to imported row
                jdbcTemplate.update(
                        "UPDATE flood_zones SET version_tag = ?, imported_at = ? WHERE zone_name = ?",
                        versionTag, java.sql.Timestamp.valueOf(importedAt), zoneName);
                imported++;
                log.info("Imported zone: name={}, risk={}", zoneName, riskLevel);
            }

        } catch (IllegalArgumentException e) {
            log.error("Import validation failed: {}", e.getMessage());
            throw e; // triggers @Transactional rollback
        } catch (Exception e) {
            log.error("Import failed: {}", e.getMessage());
            throw new IllegalArgumentException("Failed to import GeoJSON: " + e.getMessage());
        }

        log.info("GeoJSON import complete: {} zones imported", imported);
        return new ZoneImportResult(imported, errors, versionTag);
    }

    private String extractZoneName(JsonNode feature) {
        JsonNode props = feature.path("properties");
        if (props.has("zone_name")) return props.get("zone_name").asText();
        if (props.has("name")) return props.get("name").asText();
        if (props.has("zoneName")) return props.get("zoneName").asText();
        throw new IllegalArgumentException("Feature missing zone_name property");
    }

    private String extractRiskLevel(JsonNode feature) {
        JsonNode props = feature.path("properties");
        if (props.has("risk_level")) return props.get("risk_level").asText().toUpperCase();
        if (props.has("riskLevel")) return props.get("riskLevel").asText().toUpperCase();
        return "MEDIUM"; // default
    }

    private String buildOrdinateArray(JsonNode coordinates) {
        StringJoiner joiner = new StringJoiner(", ");
        double firstLon = 0, firstLat = 0;
        double lastLon = 0, lastLat = 0;

        for (int i = 0; i < coordinates.size(); i++) {
            JsonNode coord = coordinates.get(i);
            double lon = coord.get(0).asDouble();
            double lat = coord.get(1).asDouble();

            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                throw new IllegalArgumentException(
                        String.format("Invalid coordinate: [%.6f, %.6f] — must be WGS84 (SRID 4326)", lon, lat));
            }

            joiner.add(String.valueOf(lon));
            joiner.add(String.valueOf(lat));

            if (i == 0) { firstLon = lon; firstLat = lat; }
            lastLon = lon; lastLat = lat;
        }

        // Enforce ring closure
        if (firstLon != lastLon || firstLat != lastLat) {
            joiner.add(String.valueOf(firstLon));
            joiner.add(String.valueOf(firstLat));
        }

        return joiner.toString();
    }
}
