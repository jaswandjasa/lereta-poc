package com.estate.floodzoning.dto;

import com.estate.floodzoning.enums.RiskLevel;

public record FloodResponse(
        String zoneName,
        RiskLevel riskLevel,
        boolean insideFloodZone
) {}
