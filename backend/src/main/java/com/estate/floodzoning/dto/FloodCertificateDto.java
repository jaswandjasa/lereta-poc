package com.estate.floodzoning.dto;

import java.time.LocalDateTime;

public record FloodCertificateDto(
        String certificateNumber,
        String propertyName,
        double latitude,
        double longitude,
        String riskLevel,
        String zoneName,
        String nearestZone,
        double nearestDistanceMeters,
        String mapReference,
        LocalDateTime generatedAt
) {}
