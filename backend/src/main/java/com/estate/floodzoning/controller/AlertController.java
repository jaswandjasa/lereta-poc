package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.FloodAlertDto;
import com.estate.floodzoning.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<FloodAlertDto>> getAlerts() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<Map<String, String>> acknowledge(@PathVariable Long id) {
        alertService.acknowledge(id);
        return ResponseEntity.ok(Map.of("status", "acknowledged"));
    }
}
