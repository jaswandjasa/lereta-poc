package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.BulkFloodSummary;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.enums.RiskLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkFloodServiceTest {

    @Mock
    private FloodService floodService;

    @InjectMocks
    private BulkFloodService bulkFloodService;

    @Test
    @DisplayName("Should process valid CSV and return summary with correct counts")
    void shouldProcessValidCsv() {
        String csv = "property_name,lat,lon\nPlot 1,33.60,73.06\nPlot 2,33.71,73.05\n";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes());

        FloodResponse highRisk = new FloodResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true);
        FloodResponse lowRisk = new FloodResponse("No Zone", RiskLevel.LOW, false);
        NearestZoneResponse nearest = new NearestZoneResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true, 0.0);
        NearestZoneResponse nearestLow = new NearestZoneResponse("Margalla Corridor", RiskLevel.MEDIUM, false, 300.0);

        when(floodService.checkFloodRisk(33.60, 73.06)).thenReturn(highRisk);
        when(floodService.findNearestFloodZone(33.60, 73.06)).thenReturn(nearest);
        when(floodService.checkFloodRisk(33.71, 73.05)).thenReturn(lowRisk);
        when(floodService.findNearestFloodZone(33.71, 73.05)).thenReturn(nearestLow);

        BulkFloodSummary summary = bulkFloodService.processBulkCheck(file);

        assertThat(summary.totalRows()).isEqualTo(2);
        assertThat(summary.successRows()).isEqualTo(2);
        assertThat(summary.failedRows()).isEqualTo(0);
        assertThat(summary.results()).hasSize(2);
        assertThat(summary.results().get(0).riskLevel()).isEqualTo("HIGH");
        assertThat(summary.results().get(0).processed()).isTrue();
        assertThat(summary.results().get(1).riskLevel()).isEqualTo("LOW");
    }

    @Test
    @DisplayName("Should capture row-level errors without failing entire batch")
    void shouldCaptureRowLevelErrors() {
        String csv = "property_name,lat,lon\nGood Plot,33.60,73.06\nBad Plot,33.60,73.06\n";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csv.getBytes());

        FloodResponse highRisk = new FloodResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true);
        NearestZoneResponse nearest = new NearestZoneResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true, 0.0);

        when(floodService.checkFloodRisk(33.60, 73.06))
                .thenReturn(highRisk)
                .thenThrow(new RuntimeException("Spatial query timeout"));

        when(floodService.findNearestFloodZone(33.60, 73.06)).thenReturn(nearest);

        BulkFloodSummary summary = bulkFloodService.processBulkCheck(file);

        assertThat(summary.totalRows()).isEqualTo(2);
        assertThat(summary.successRows()).isEqualTo(1);
        assertThat(summary.failedRows()).isEqualTo(1);
        assertThat(summary.results().get(0).processed()).isTrue();
        assertThat(summary.results().get(1).processed()).isFalse();
        assertThat(summary.results().get(1).errorMessage()).isEqualTo("Spatial query timeout");
    }

    @Test
    @DisplayName("Should reject file exceeding 2 MB")
    void shouldRejectOversizedFile() {
        byte[] largeContent = new byte[3 * 1024 * 1024]; // 3 MB
        MockMultipartFile file = new MockMultipartFile("file", "huge.csv", "text/csv", largeContent);

        assertThatThrownBy(() -> bulkFloodService.processBulkCheck(file))
                .isInstanceOf(BulkFloodService.FileTooLargeException.class)
                .hasMessageContaining("exceeds maximum of 2 MB");
    }

    @Test
    @DisplayName("Should throw on invalid CSV format")
    void shouldThrowOnInvalidCsv() {
        String badCsv = "property_name,lat,lon\nPlot 1,not_a_number,73.06\n";
        MockMultipartFile file = new MockMultipartFile("file", "bad.csv", "text/csv", badCsv.getBytes());

        assertThatThrownBy(() -> bulkFloodService.processBulkCheck(file))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
