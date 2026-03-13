package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.PropertyComparisonDto;
import com.estate.floodzoning.service.ComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class ComparisonController {

    private final ComparisonService comparisonService;

    @GetMapping("/compare")
    public ResponseEntity<PropertyComparisonDto> compare(
            @RequestParam Long id1, @RequestParam Long id2) {
        if (id1.equals(id2)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(comparisonService.compare(id1, id2));
    }
}
