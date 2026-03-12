package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.ZoneImportResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZoneImportServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ZoneImportService zoneImportService;

    @BeforeEach
    void setUp() {
        zoneImportService = new ZoneImportService(jdbcTemplate, new ObjectMapper());
    }

    @Test
    @DisplayName("Should import valid GeoJSON FeatureCollection")
    void shouldImportValidFeatureCollection() {
        String geojson = """
                {
                  "type": "FeatureCollection",
                  "features": [{
                    "type": "Feature",
                    "properties": {"zone_name": "Test Zone", "risk_level": "HIGH"},
                    "geometry": {
                      "type": "Polygon",
                      "coordinates": [[[73.0, 33.5], [73.1, 33.5], [73.1, 33.6], [73.0, 33.6], [73.0, 33.5]]]
                    }
                  }]
                }
                """;
        MockMultipartFile file = new MockMultipartFile("file", "zones.geojson", "application/json", geojson.getBytes());

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("Test Zone"))).thenReturn(0);
        when(jdbcTemplate.update(anyString(), eq("Test Zone"), eq("HIGH"), anyString())).thenReturn(1);

        ZoneImportResult result = zoneImportService.importGeoJson(file);

        assertThat(result.zonesImported()).isEqualTo(1);
        assertThat(result.errors()).isEmpty();
    }

    @Test
    @DisplayName("Should reject duplicate zone name")
    void shouldRejectDuplicateZoneName() {
        String geojson = """
                {
                  "type": "FeatureCollection",
                  "features": [{
                    "type": "Feature",
                    "properties": {"zone_name": "Nullah Lai Flood Plain", "risk_level": "HIGH"},
                    "geometry": {
                      "type": "Polygon",
                      "coordinates": [[[73.0, 33.5], [73.1, 33.5], [73.1, 33.6], [73.0, 33.6], [73.0, 33.5]]]
                    }
                  }]
                }
                """;
        MockMultipartFile file = new MockMultipartFile("file", "zones.geojson", "application/json", geojson.getBytes());

        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("Nullah Lai Flood Plain"))).thenReturn(1);

        assertThatThrownBy(() -> zoneImportService.importGeoJson(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate zone name");
    }

    @Test
    @DisplayName("Should reject non-Polygon geometry")
    void shouldRejectNonPolygonGeometry() {
        String geojson = """
                {
                  "type": "FeatureCollection",
                  "features": [{
                    "type": "Feature",
                    "properties": {"zone_name": "Test", "risk_level": "LOW"},
                    "geometry": {
                      "type": "Point",
                      "coordinates": [73.0, 33.5]
                    }
                  }]
                }
                """;
        MockMultipartFile file = new MockMultipartFile("file", "zones.geojson", "application/json", geojson.getBytes());

        assertThatThrownBy(() -> zoneImportService.importGeoJson(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only Polygon geometry supported");
    }

    @Test
    @DisplayName("Should reject missing zone_name property")
    void shouldRejectMissingZoneName() {
        String geojson = """
                {
                  "type": "FeatureCollection",
                  "features": [{
                    "type": "Feature",
                    "properties": {"risk_level": "HIGH"},
                    "geometry": {
                      "type": "Polygon",
                      "coordinates": [[[73.0, 33.5], [73.1, 33.5], [73.1, 33.6], [73.0, 33.6], [73.0, 33.5]]]
                    }
                  }]
                }
                """;
        MockMultipartFile file = new MockMultipartFile("file", "zones.geojson", "application/json", geojson.getBytes());

        assertThatThrownBy(() -> zoneImportService.importGeoJson(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("missing zone_name");
    }
}
