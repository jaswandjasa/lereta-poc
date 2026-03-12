package com.estate.floodzoning.dto;

public record FloodZoneDto(
        Long id,
        String zoneName,
        String riskLevel,
        String geojson
) {}
