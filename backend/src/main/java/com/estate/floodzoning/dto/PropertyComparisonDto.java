package com.estate.floodzoning.dto;

public record PropertyComparisonDto(
        PropertySnapshot property1,
        PropertySnapshot property2
) {
    public record PropertySnapshot(
            Long propertyId,
            String propertyName,
            String riskLevel,
            String nearestZone,
            long certificateCount,
            long activeAlerts
    ) {}
}
