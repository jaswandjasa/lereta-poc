package com.estate.floodzoning.spatial;

import com.estate.floodzoning.domain.FloodZone;
import com.estate.floodzoning.repository.FloodZoneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpatialQueryService {

    private final FloodZoneRepository floodZoneRepository;

    public List<FloodZone> findContainingZones(double lat, double lon) {
        long start = System.currentTimeMillis();
        List<FloodZone> zones = floodZoneRepository.findContainingZones(lon, lat);
        long elapsed = System.currentTimeMillis() - start;
        log.debug("SDO_CONTAINS query completed in {}ms, found {} zones", elapsed, zones.size());
        return zones;
    }

    public List<FloodZone> findZonesWithinDistance(double lat, double lon) {
        long start = System.currentTimeMillis();
        List<FloodZone> zones = floodZoneRepository.findZonesWithinDistance(lon, lat);
        long elapsed = System.currentTimeMillis() - start;
        log.debug("SDO_WITHIN_DISTANCE query completed in {}ms, found {} zones", elapsed, zones.size());
        return zones;
    }

    public Optional<FloodZone> findNearestZone(double lat, double lon) {
        long start = System.currentTimeMillis();
        List<FloodZone> zones = floodZoneRepository.findNearestZone(lon, lat);
        long elapsed = System.currentTimeMillis() - start;
        log.debug("SDO_NN query completed in {}ms, found {} zones", elapsed, zones.size());
        return zones.stream().findFirst();
    }
}
