package com.estate.floodzoning.service;

import com.estate.floodzoning.domain.Property;
import com.estate.floodzoning.domain.PropertyMonitoring;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.MonitoringStatusDto;
import com.estate.floodzoning.enums.RiskLevel;
import com.estate.floodzoning.repository.PropertyMonitoringRepository;
import com.estate.floodzoning.repository.PropertyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MonitoringServiceTest {

    @Mock
    private PropertyMonitoringRepository monitoringRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private FloodService floodService;

    @InjectMocks
    private MonitoringService monitoringService;

    private PropertyMonitoring createRecord(Long id, Long propertyId, String currentZone, String currentRisk) {
        PropertyMonitoring m = new PropertyMonitoring();
        m.setId(id);
        m.setPropertyId(propertyId);
        m.setCurrentZone(currentZone);
        m.setCurrentRisk(currentRisk);
        m.setMonitoringEnabled(true);
        m.setStatusChanged(false);
        return m;
    }

    private Property createProperty(Long id, String name, double lat, double lon) {
        Property p = new Property();
        p.setId(id);
        p.setPropertyName(name);
        p.setLatitude(lat);
        p.setLongitude(lon);
        return p;
    }

    @Test
    @DisplayName("Should detect status change when flood zone classification changes")
    void shouldDetectChangeWhenZoneChanges() {
        PropertyMonitoring record = createRecord(1L, 1L, "No Zone", "LOW");
        Property property = createProperty(1L, "Riverside Heights", 33.60, 73.06);
        FloodResponse newResult = new FloodResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true);

        when(monitoringRepository.findByMonitoringEnabledTrue()).thenReturn(List.of(record));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(floodService.checkFloodRisk(33.60, 73.06)).thenReturn(newResult);

        int changes = monitoringService.runMonitoringCycle();

        assertThat(changes).isEqualTo(1);
        assertThat(record.getCurrentZone()).isEqualTo("Nullah Lai Flood Plain");
        assertThat(record.getCurrentRisk()).isEqualTo("HIGH");
        assertThat(record.getLastZone()).isEqualTo("No Zone");
        assertThat(record.getLastRisk()).isEqualTo("LOW");
        assertThat(record.getStatusChanged()).isTrue();
        verify(monitoringRepository).save(any(PropertyMonitoring.class));
    }

    @Test
    @DisplayName("Should not flag change when classification stays the same")
    void shouldNotFlagChangeWhenSame() {
        PropertyMonitoring record = createRecord(1L, 1L, "Nullah Lai Flood Plain", "HIGH");
        Property property = createProperty(1L, "Riverside Heights", 33.60, 73.06);
        FloodResponse sameResult = new FloodResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true);

        when(monitoringRepository.findByMonitoringEnabledTrue()).thenReturn(List.of(record));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(floodService.checkFloodRisk(33.60, 73.06)).thenReturn(sameResult);

        int changes = monitoringService.runMonitoringCycle();

        assertThat(changes).isEqualTo(0);
        assertThat(record.getStatusChanged()).isFalse();
    }

    @Test
    @DisplayName("Should return zero changes when no monitoring records exist")
    void shouldReturnZeroWhenEmpty() {
        when(monitoringRepository.findByMonitoringEnabledTrue()).thenReturn(Collections.emptyList());

        int changes = monitoringService.runMonitoringCycle();

        assertThat(changes).isEqualTo(0);
    }

    @Test
    @DisplayName("Should return monitoring status DTOs with property names")
    void shouldReturnStatusDtos() {
        PropertyMonitoring record = createRecord(1L, 1L, "No Zone", "LOW");
        Property property = createProperty(1L, "Riverside Heights", 33.60, 73.06);

        when(monitoringRepository.findAll()).thenReturn(List.of(record));
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        List<MonitoringStatusDto> results = monitoringService.getMonitoringStatus();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).propertyName()).isEqualTo("Riverside Heights");
        assertThat(results.get(0).monitoringEnabled()).isTrue();
    }
}
