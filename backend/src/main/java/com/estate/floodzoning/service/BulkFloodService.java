package com.estate.floodzoning.service;

// FUTURE: This module can be extracted to a standalone Batch Processing Microservice.
// Dependencies: FloodService (→ GIS Core API)
// Extraction order: 2nd (stateless CSV processing)

import com.estate.floodzoning.dto.BulkFloodRequest;
import com.estate.floodzoning.dto.BulkFloodResult;
import com.estate.floodzoning.dto.BulkFloodSummary;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkFloodService {

    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2 MB

    private final FloodService floodService;

    public BulkFloodSummary processBulkCheck(MultipartFile file) {
        log.info("Processing bulk flood check, file={}, size={} bytes", file.getOriginalFilename(), file.getSize());

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileTooLargeException("File size " + file.getSize() + " bytes exceeds maximum of 2 MB");
        }

        List<BulkFloodRequest> rows = parseCsv(file);
        List<BulkFloodResult> results = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (BulkFloodRequest row : rows) {
            try {
                FloodResponse flood = floodService.checkFloodRisk(row.latitude(), row.longitude());
                NearestZoneResponse nearest = floodService.findNearestFloodZone(row.latitude(), row.longitude());

                results.add(new BulkFloodResult(
                        row.propertyName(),
                        flood.riskLevel().name(),
                        flood.insideFloodZone(),
                        nearest.zoneName(),
                        true,
                        null
                ));
                successCount++;
            } catch (Exception e) {
                log.warn("Failed to process row '{}': {}", row.propertyName(), e.getMessage());
                results.add(new BulkFloodResult(
                        row.propertyName(),
                        null,
                        false,
                        null,
                        false,
                        e.getMessage()
                ));
                failCount++;
            }
        }

        log.info("Bulk processing complete: total={}, success={}, failed={}", rows.size(), successCount, failCount);
        return new BulkFloodSummary(rows.size(), successCount, failCount, results);
    }

    private List<BulkFloodRequest> parseCsv(MultipartFile file) {
        List<BulkFloodRequest> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader("property_name", "lat", "lon")
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .setIgnoreEmptyLines(true)
                     .build()
                     .parse(reader)) {

            for (CSVRecord record : parser) {
                String name = record.get("property_name");
                double lat = Double.parseDouble(record.get("lat"));
                double lon = Double.parseDouble(record.get("lon"));

                if (lat < -90 || lat > 90) {
                    throw new IllegalArgumentException("Invalid latitude: " + lat);
                }
                if (lon < -180 || lon > 180) {
                    throw new IllegalArgumentException("Invalid longitude: " + lon);
                }

                rows.add(new BulkFloodRequest(name, lat, lon));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("CSV contains invalid numeric value: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse CSV: " + e.getMessage());
        }

        return rows;
    }

    public static class FileTooLargeException extends RuntimeException {
        public FileTooLargeException(String message) {
            super(message);
        }
    }
}
