package com.estate.floodzoning.dto;

public record PortfolioDashboardDto(
        long highRiskCount,
        long mediumRiskCount,
        long lowRiskCount,
        long alertsToday,
        long todayCertificates,
        long totalCertificates,
        long monitoredChanged
) {}
