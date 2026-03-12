package com.estate.floodzoning.dto;

import com.estate.floodzoning.enums.RiskLevel;

public record NearestZoneResponse(
        String zoneName,
        RiskLevel riskLevel,
        boolean insideFloodZone,
        double nearestDistanceMeters
) {}
