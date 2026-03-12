package com.estate.floodzoning.dto;

import java.util.List;

public record BulkFloodSummary(
        int totalRows,
        int successRows,
        int failedRows,
        List<BulkFloodResult> results
) {}
