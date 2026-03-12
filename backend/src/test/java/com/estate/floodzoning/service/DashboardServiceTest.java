package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.PortfolioDashboardDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Should aggregate correct portfolio counts")
    void shouldAggregatePortfolioCounts() {
        // Risk counts
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("HIGH"))).thenReturn(2L);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("MEDIUM"))).thenReturn(1L);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("LOW"))).thenReturn(3L);

        // Count queries without params (alerts today, today certs, total certs, monitored changed)
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class)))
                .thenReturn(5L)   // alertsToday
                .thenReturn(2L)   // todayCertificates
                .thenReturn(10L)  // totalCertificates
                .thenReturn(1L);  // monitoredChanged

        PortfolioDashboardDto result = dashboardService.getPortfolioDashboard();

        assertThat(result.highRiskCount()).isEqualTo(2);
        assertThat(result.mediumRiskCount()).isEqualTo(1);
        assertThat(result.lowRiskCount()).isEqualTo(3);
        assertThat(result.alertsToday()).isEqualTo(5);
        assertThat(result.todayCertificates()).isEqualTo(2);
        assertThat(result.totalCertificates()).isEqualTo(10);
        assertThat(result.monitoredChanged()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should handle zero counts gracefully")
    void shouldHandleZeroCounts() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("HIGH"))).thenReturn(0L);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("MEDIUM"))).thenReturn(0L);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class), eq("LOW"))).thenReturn(0L);
        when(jdbcTemplate.queryForObject(anyString(), eq(Long.class))).thenReturn(0L);

        PortfolioDashboardDto result = dashboardService.getPortfolioDashboard();

        assertThat(result.highRiskCount()).isZero();
        assertThat(result.mediumRiskCount()).isZero();
        assertThat(result.lowRiskCount()).isZero();
        assertThat(result.alertsToday()).isZero();
        assertThat(result.todayCertificates()).isZero();
        assertThat(result.totalCertificates()).isZero();
        assertThat(result.monitoredChanged()).isZero();
    }
}
