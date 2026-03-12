package com.estate.floodzoning.dto;

public record BulkFloodResult(
        String propertyName,
        String riskLevel,
        boolean insideFloodZone,
        String nearestZone,
        boolean processed,
        String errorMessage
) {}
