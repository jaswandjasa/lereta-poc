package com.estate.floodzoning.dto;

public record BufferResponse(
        String geojson,
        String bufferHigh,
        String bufferMedium,
        String bufferLow
) {}
