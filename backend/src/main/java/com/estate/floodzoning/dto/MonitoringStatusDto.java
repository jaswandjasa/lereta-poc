package com.estate.floodzoning.dto;

import java.time.LocalDateTime;

public record MonitoringStatusDto(
        Long id,
        Long propertyId,
        String propertyName,
        String lastZone,
        String currentZone,
        String lastRisk,
        String currentRisk,
        LocalDateTime lastChecked,
        boolean statusChanged,
        boolean monitoringEnabled
) {}
