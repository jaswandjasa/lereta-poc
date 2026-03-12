package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.service.FloodService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flood")
@RequiredArgsConstructor
@Validated
public class FloodController {

    private final FloodService floodService;

    @GetMapping("/check")
    public ResponseEntity<FloodResponse> checkFloodRisk(
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lon) {

        return ResponseEntity.ok(floodService.checkFloodRisk(lat, lon));
    }

    @GetMapping("/nearest")
    public ResponseEntity<NearestZoneResponse> findNearestZone(
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double lat,
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double lon) {

        return ResponseEntity.ok(floodService.findNearestFloodZone(lat, lon));
    }
}
