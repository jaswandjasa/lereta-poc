package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.RiskHistoryDto;
import com.estate.floodzoning.service.RiskHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class RiskHistoryController {

    private final RiskHistoryService riskHistoryService;

    @GetMapping("/{propertyId}")
    public ResponseEntity<List<RiskHistoryDto>> getHistory(@PathVariable Long propertyId) {
        return ResponseEntity.ok(riskHistoryService.getHistory(propertyId));
    }
}
