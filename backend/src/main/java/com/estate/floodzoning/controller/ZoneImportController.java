package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.ZoneImportResult;
import com.estate.floodzoning.service.ZoneImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneImportController {

    private final ZoneImportService zoneImportService;

    @PostMapping("/import")
    public ResponseEntity<ZoneImportResult> importGeoJson(@RequestParam("file") MultipartFile file) {
        ZoneImportResult result = zoneImportService.importGeoJson(file);
        return ResponseEntity.ok(result);
    }
}
