package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.BulkFloodSummary;
import com.estate.floodzoning.service.BulkFloodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/flood")
@RequiredArgsConstructor
public class BulkFloodController {

    private final BulkFloodService bulkFloodService;

    @PostMapping("/bulk-check")
    public ResponseEntity<BulkFloodSummary> bulkCheck(@RequestParam("file") MultipartFile file) {
        BulkFloodSummary summary = bulkFloodService.processBulkCheck(file);
        return ResponseEntity.ok(summary);
    }
}
