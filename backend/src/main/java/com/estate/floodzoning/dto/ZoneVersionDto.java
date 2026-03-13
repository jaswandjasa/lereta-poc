package com.estate.floodzoning.dto;

import java.time.LocalDateTime;

public record ZoneVersionDto(
        String versionTag,
        long zoneCount,
        LocalDateTime importedAt
) {}
