package com.estate.floodzoning.service;

import com.estate.floodzoning.domain.FloodAlert;
import com.estate.floodzoning.domain.Property;
import com.estate.floodzoning.dto.FloodAlertDto;
import com.estate.floodzoning.enums.AlertType;
import com.estate.floodzoning.repository.FloodAlertRepository;
import com.estate.floodzoning.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final FloodAlertRepository alertRepository;
    private final PropertyRepository propertyRepository;

    /**
     * Creates an alert only if the latest alert for this property does not already
     * represent the same transition (duplicate prevention — Correction 1).
     */
    @Transactional
    public void createAlert(Long propertyId, String oldRisk, String newRisk,
                            String oldZone, String newZone) {

        // Duplicate prevention: check latest alert for this property
        Optional<FloodAlert> latest = alertRepository.findTopByPropertyIdOrderByCreatedAtDesc(propertyId);
        if (latest.isPresent()) {
            FloodAlert last = latest.get();
            if (Objects.equals(last.getOldRisk(), oldRisk)
                    && Objects.equals(last.getNewRisk(), newRisk)
                    && Objects.equals(last.getOldZone(), oldZone)
                    && Objects.equals(last.getNewZone(), newZone)) {
                log.debug("Duplicate alert suppressed for propertyId={}, transition={} → {}",
                        propertyId, oldRisk, newRisk);
                return;
            }
        }

        String alertType = deriveAlertType(oldRisk, newRisk, oldZone, newZone);

        FloodAlert alert = new FloodAlert();
        alert.setPropertyId(propertyId);
        alert.setOldRisk(oldRisk);
        alert.setNewRisk(newRisk);
        alert.setOldZone(oldZone);
        alert.setNewZone(newZone);
        alert.setAlertType(alertType);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setAcknowledged(false);
        alertRepository.save(alert);

        log.info("ALERT created: propertyId={}, type={}, {} → {}", propertyId, alertType, oldRisk, newRisk);
    }

    public List<FloodAlertDto> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    public List<FloodAlertDto> getActiveAlerts() {
        return alertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void acknowledge(Long alertId) {
        FloodAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new com.estate.floodzoning.exception.ResourceNotFoundException(
                        "Alert not found: " + alertId));
        alert.setAcknowledged(true);
        alert.setAcknowledgedAt(LocalDateTime.now());
        alertRepository.save(alert);
        log.info("Alert acknowledged: id={}", alertId);
    }

    String deriveAlertType(String oldRisk, String newRisk, String oldZone, String newZone) {
        if (oldRisk != null && newRisk != null && !oldRisk.equals(newRisk)) {
            int oldOrdinal = riskOrdinal(oldRisk);
            int newOrdinal = riskOrdinal(newRisk);
            if (newOrdinal > oldOrdinal) {
                return AlertType.RISK_INCREASE.name();
            } else {
                return AlertType.RISK_DECREASE.name();
            }
        }
        return AlertType.ZONE_CHANGED.name();
    }

    private int riskOrdinal(String risk) {
        return switch (risk) {
            case "HIGH" -> 3;
            case "MEDIUM" -> 2;
            case "LOW" -> 1;
            default -> 0;
        };
    }

    private FloodAlertDto toDto(FloodAlert a) {
        String propertyName = propertyRepository.findById(a.getPropertyId())
                .map(Property::getPropertyName)
                .orElse("Unknown");

        return new FloodAlertDto(
                a.getId(),
                a.getPropertyId(),
                propertyName,
                a.getOldRisk(),
                a.getNewRisk(),
                a.getOldZone(),
                a.getNewZone(),
                a.getAlertType(),
                a.getCreatedAt(),
                Boolean.TRUE.equals(a.getAcknowledged()),
                a.getAcknowledgedAt()
        );
    }
}
