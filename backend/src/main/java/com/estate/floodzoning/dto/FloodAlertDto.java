package com.estate.floodzoning.dto;

import java.time.LocalDateTime;

public record FloodAlertDto(
        Long id,
        Long propertyId,
        String propertyName,
        String oldRisk,
        String newRisk,
        String oldZone,
        String newZone,
        String alertType,
        LocalDateTime createdAt,
        boolean acknowledged,
        LocalDateTime acknowledgedAt
) {}
