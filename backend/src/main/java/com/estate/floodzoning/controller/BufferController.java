package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.BufferResponse;
import com.estate.floodzoning.service.BufferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/flood")
@RequiredArgsConstructor
public class BufferController {

    private final BufferService bufferService;

    @GetMapping("/buffer")
    public ResponseEntity<BufferResponse> getBuffer(
            @RequestParam Double lat, @RequestParam Double lon) {
        return ResponseEntity.ok(bufferService.generateBuffer(lat, lon));
    }
}
