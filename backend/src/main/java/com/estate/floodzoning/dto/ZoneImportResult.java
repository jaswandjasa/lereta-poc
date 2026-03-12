package com.estate.floodzoning.dto;

import java.util.List;

public record ZoneImportResult(
        int zonesImported,
        List<String> errors
) {}
