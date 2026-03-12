package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.PortfolioDashboardDto;
import com.estate.floodzoning.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/portfolio")
    public ResponseEntity<PortfolioDashboardDto> getPortfolio() {
        return ResponseEntity.ok(dashboardService.getPortfolioDashboard());
    }
}
