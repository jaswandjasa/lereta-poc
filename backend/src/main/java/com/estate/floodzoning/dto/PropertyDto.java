package com.estate.floodzoning.dto;

public record PropertyDto(
        Long id,
        String propertyName,
        Double latitude,
        Double longitude
) {}
