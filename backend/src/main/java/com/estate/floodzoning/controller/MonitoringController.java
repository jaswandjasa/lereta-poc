package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.MonitoringStatusDto;
import com.estate.floodzoning.service.MonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping
    public ResponseEntity<List<MonitoringStatusDto>> getMonitoringStatus() {
        return ResponseEntity.ok(monitoringService.getMonitoringStatus());
    }

    @GetMapping("/run")
    public ResponseEntity<Map<String, Object>> triggerMonitoringCycle() {
        int changes = monitoringService.runMonitoringCycle();
        return ResponseEntity.ok(Map.of(
                "status", "completed",
                "changesDetected", changes
        ));
    }
}
