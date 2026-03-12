package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.FloodZoneDto;
import com.estate.floodzoning.service.FloodZoneService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class FloodZoneController {

    private final FloodZoneService floodZoneService;

    @GetMapping
    public ResponseEntity<List<FloodZoneDto>> getAllZones() {
        return ResponseEntity.ok(floodZoneService.getAllZones());
    }
}
