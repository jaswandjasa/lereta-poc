package com.estate.floodzoning.dto;

public record BulkFloodRequest(
        String propertyName,
        double latitude,
        double longitude
) {}
