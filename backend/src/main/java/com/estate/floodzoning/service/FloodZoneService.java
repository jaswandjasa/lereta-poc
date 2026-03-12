package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.FloodZoneDto;
import com.estate.floodzoning.repository.FloodZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FloodZoneService {

    private final FloodZoneRepository repository;

    public List<FloodZoneDto> getAllZones() {
        log.debug("Fetching all flood zones for map rendering");
        return repository.findAll()
                .stream()
                .map(zone -> new FloodZoneDto(
                        zone.getId(),
                        zone.getZoneName(),
                        zone.getRiskLevel(),
                        zone.getGeojson()))
                .toList();
    }
}
