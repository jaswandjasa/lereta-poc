package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.dto.PropertyComparisonDto;
import com.estate.floodzoning.dto.PropertyDto;
import com.estate.floodzoning.enums.RiskLevel;
import com.estate.floodzoning.repository.CertificateAuditRepository;
import com.estate.floodzoning.repository.FloodAlertRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ComparisonServiceTest {

    @Mock
    private PropertyService propertyService;

    @Mock
    private FloodService floodService;

    @Mock
    private CertificateAuditRepository certificateAuditRepository;

    @Mock
    private FloodAlertRepository floodAlertRepository;

    @InjectMocks
    private ComparisonService comparisonService;

    @Test
    @DisplayName("Should return comparison for two different properties")
    void shouldCompareProperties() {
        PropertyDto p1 = new PropertyDto(1L, "Riverside Heights", 33.60, 73.06);
        PropertyDto p2 = new PropertyDto(2L, "Lake View", 33.70, 73.12);

        when(propertyService.getPropertyById(1L)).thenReturn(p1);
        when(propertyService.getPropertyById(2L)).thenReturn(p2);

        when(floodService.checkFloodRisk(33.60, 73.06))
                .thenReturn(new FloodResponse("Zone A", RiskLevel.HIGH, true));
        when(floodService.checkFloodRisk(33.70, 73.12))
                .thenReturn(new FloodResponse("Zone B", RiskLevel.LOW, false));

        when(floodService.findNearestFloodZone(33.60, 73.06))
                .thenReturn(new NearestZoneResponse("Zone A", RiskLevel.HIGH, true, 0.0));
        when(floodService.findNearestFloodZone(33.70, 73.12))
                .thenReturn(new NearestZoneResponse("Zone B", RiskLevel.LOW, false, 500.0));

        when(certificateAuditRepository.countByPropertyId(1L)).thenReturn(3L);
        when(certificateAuditRepository.countByPropertyId(2L)).thenReturn(1L);

        when(floodAlertRepository.countByPropertyIdAndAcknowledgedFalse(1L)).thenReturn(2L);
        when(floodAlertRepository.countByPropertyIdAndAcknowledgedFalse(2L)).thenReturn(0L);

        PropertyComparisonDto result = comparisonService.compare(1L, 2L);

        assertThat(result.property1().propertyName()).isEqualTo("Riverside Heights");
        assertThat(result.property1().riskLevel()).isEqualTo("HIGH");
        assertThat(result.property1().certificateCount()).isEqualTo(3);
        assertThat(result.property1().activeAlerts()).isEqualTo(2);

        assertThat(result.property2().propertyName()).isEqualTo("Lake View");
        assertThat(result.property2().riskLevel()).isEqualTo("LOW");
        assertThat(result.property2().certificateCount()).isEqualTo(1);
        assertThat(result.property2().activeAlerts()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should reject identical id1=id2")
    void shouldRejectSameProperty() {
        assertThatThrownBy(() -> comparisonService.compare(1L, 1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cannot compare a property with itself");
    }
}
