package com.estate.floodzoning.service;

import com.estate.floodzoning.domain.FloodZone;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.enums.RiskLevel;
import com.estate.floodzoning.spatial.SpatialQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FloodService {

    private final SpatialQueryService spatialQueryService;

    public FloodResponse checkFloodRisk(double lat, double lon) {
        log.info("Checking flood risk for lat={}, lon={}", lat, lon);

        List<FloodZone> containingZones = spatialQueryService.findContainingZones(lat, lon);

        if (!containingZones.isEmpty()) {
            FloodZone zone = containingZones.get(0);
            log.info("Point is INSIDE flood zone: {}", zone.getZoneName());
            return new FloodResponse(
                    zone.getZoneName(),
                    RiskLevel.valueOf(zone.getRiskLevel()),
                    true
            );
        }

        List<FloodZone> nearbyZones = spatialQueryService.findZonesWithinDistance(lat, lon);

        if (!nearbyZones.isEmpty()) {
            FloodZone zone = nearbyZones.get(0);
            log.info("Point is NEAR flood zone: {} (within 500m)", zone.getZoneName());
            return new FloodResponse(
                    zone.getZoneName(),
                    RiskLevel.MEDIUM,
                    false
            );
        }

        log.info("Point is outside all flood zones");
        return new FloodResponse(
                "No Zone",
                RiskLevel.LOW,
                false
        );
    }

    public NearestZoneResponse findNearestFloodZone(double lat, double lon) {
        log.info("Finding nearest flood zone for lat={}, lon={}", lat, lon);

        List<FloodZone> containingZones = spatialQueryService.findContainingZones(lat, lon);

        if (!containingZones.isEmpty()) {
            FloodZone zone = containingZones.get(0);
            return new NearestZoneResponse(
                    zone.getZoneName(),
                    RiskLevel.valueOf(zone.getRiskLevel()),
                    true,
                    0.0
            );
        }

        Optional<FloodZone> nearest = spatialQueryService.findNearestZone(lat, lon);

        if (nearest.isPresent()) {
            FloodZone zone = nearest.get();
            return new NearestZoneResponse(
                    zone.getZoneName(),
                    RiskLevel.MEDIUM,
                    false,
                    -1.0
            );
        }

        return new NearestZoneResponse(
                "No Zone",
                RiskLevel.LOW,
                false,
                -1.0
        );
    }
}
