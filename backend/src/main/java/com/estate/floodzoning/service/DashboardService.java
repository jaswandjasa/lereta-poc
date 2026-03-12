package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.PortfolioDashboardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public PortfolioDashboardDto getPortfolioDashboard() {
        log.debug("Building portfolio dashboard");

        long highRisk = countByRisk("HIGH");
        long mediumRisk = countByRisk("MEDIUM");
        long lowRisk = countByRisk("LOW");

        long alertsToday = queryCount(
                "SELECT COUNT(*) FROM flood_alerts WHERE created_at >= TRUNC(SYSDATE)");

        long todayCerts = queryCount(
                "SELECT COUNT(*) FROM certificate_audit WHERE generated_at >= TRUNC(SYSDATE)");

        long totalCerts = queryCount(
                "SELECT COUNT(*) FROM certificate_audit");

        long monitoredChanged = queryCount(
                "SELECT COUNT(*) FROM property_monitoring WHERE status_changed = 1");

        return new PortfolioDashboardDto(
                highRisk, mediumRisk, lowRisk,
                alertsToday, todayCerts, totalCerts, monitoredChanged);
    }

    private long countByRisk(String riskLevel) {
        Long count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM property_monitoring WHERE current_risk = ? AND monitoring_enabled = 1",
                Long.class, riskLevel);
        return count != null ? count : 0;
    }

    private long queryCount(String sql) {
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        return count != null ? count : 0;
    }
}
