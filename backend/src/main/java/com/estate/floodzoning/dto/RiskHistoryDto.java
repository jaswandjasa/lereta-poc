package com.estate.floodzoning.dto;

import java.time.LocalDateTime;

public record RiskHistoryDto(
        Long id,
        Long propertyId,
        String oldRisk,
        String newRisk,
        String oldZone,
        String newZone,
        LocalDateTime changedAt,
        String triggerSource
) {}
