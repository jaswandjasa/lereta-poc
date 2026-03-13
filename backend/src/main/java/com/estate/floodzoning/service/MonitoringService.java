package com.estate.floodzoning.service;

// FUTURE: This module can be extracted to a standalone Monitoring Microservice.
// Dependencies: FloodService (→ GIS Core API), PropertyService (→ Property API)
// Extraction order: 3rd (most coupled to GIS Core)

import com.estate.floodzoning.domain.Property;
import com.estate.floodzoning.domain.PropertyMonitoring;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.MonitoringStatusDto;
import com.estate.floodzoning.exception.ResourceNotFoundException;
import com.estate.floodzoning.repository.PropertyMonitoringRepository;
import com.estate.floodzoning.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonitoringService {

    private final PropertyMonitoringRepository monitoringRepository;
    private final PropertyRepository propertyRepository;
    private final FloodService floodService;
    private final AlertService alertService;
    private final RiskHistoryService riskHistoryService;

    public List<MonitoringStatusDto> getMonitoringStatus() {
        log.debug("Fetching all monitoring records");
        return monitoringRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    @Scheduled(cron = "${monitoring.cron}")
    public int runMonitoringCycle() {
        log.info("Starting monitoring cycle");
        List<PropertyMonitoring> activeRecords = monitoringRepository.findByMonitoringEnabledTrue();
        log.info("Processing {} enabled monitoring records", activeRecords.size());

        int changesDetected = 0;

        for (PropertyMonitoring record : activeRecords) {
            try {
                Property property = propertyRepository.findById(record.getPropertyId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Property not found: " + record.getPropertyId()));

                FloodResponse result = floodService.checkFloodRisk(
                        property.getLatitude(), property.getLongitude());

                String previousZone = record.getCurrentZone();
                String previousRisk = record.getCurrentRisk();
                String newZone = result.zoneName();
                String newRisk = result.riskLevel().name();

                record.setLastZone(previousZone);
                record.setLastRisk(previousRisk);
                record.setCurrentZone(newZone);
                record.setCurrentRisk(newRisk);
                record.setLastChecked(LocalDateTime.now());

                boolean changed = !newZone.equals(previousZone) || !newRisk.equals(previousRisk);
                record.setStatusChanged(changed);

                if (changed) {
                    changesDetected++;
                    log.warn("CHANGE DETECTED — property={}, zone: {} → {}, risk: {} → {}",
                            record.getPropertyId(), previousZone, newZone, previousRisk, newRisk);
                    alertService.createAlert(record.getPropertyId(),
                            previousRisk, newRisk, previousZone, newZone);
                    riskHistoryService.recordChange(record.getPropertyId(),
                            previousRisk, newRisk, previousZone, newZone, "MONITORING");
                } else {
                    log.debug("No change — property={}, zone={}, risk={}",
                            record.getPropertyId(), newZone, newRisk);
                }

                monitoringRepository.save(record);
            } catch (Exception e) {
                log.error("Failed to monitor property={}: {}", record.getPropertyId(), e.getMessage());
            }
        }

        log.info("Monitoring cycle complete: {} records processed, {} changes detected",
                activeRecords.size(), changesDetected);
        return changesDetected;
    }

    private MonitoringStatusDto toDto(PropertyMonitoring m) {
        String propertyName = propertyRepository.findById(m.getPropertyId())
                .map(Property::getPropertyName)
                .orElse("Unknown");

        return new MonitoringStatusDto(
                m.getId(),
                m.getPropertyId(),
                propertyName,
                m.getLastZone(),
                m.getCurrentZone(),
                m.getLastRisk(),
                m.getCurrentRisk(),
                m.getLastChecked(),
                Boolean.TRUE.equals(m.getStatusChanged()),
                Boolean.TRUE.equals(m.getMonitoringEnabled())
        );
    }
}
