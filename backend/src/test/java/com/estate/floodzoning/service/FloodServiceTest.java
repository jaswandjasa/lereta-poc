package com.estate.floodzoning.service;

import com.estate.floodzoning.domain.FloodZone;
import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.enums.RiskLevel;
import com.estate.floodzoning.spatial.SpatialQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FloodServiceTest {

    @Mock
    private SpatialQueryService spatialQueryService;

    @InjectMocks
    private FloodService floodService;

    private FloodZone createZone(Long id, String name, String risk) {
        FloodZone zone = new FloodZone();
        zone.setId(id);
        zone.setZoneName(name);
        zone.setRiskLevel(risk);
        zone.setGeojson("{}");
        return zone;
    }

    @Test
    @DisplayName("Should return HIGH risk when point is inside a flood zone")
    void shouldReturnHighRiskWhenInsideZone() {
        double lat = 33.60;
        double lon = 73.06;
        FloodZone zone = createZone(1L, "Nullah Lai Flood Plain", "HIGH");

        when(spatialQueryService.findContainingZones(lat, lon))
                .thenReturn(List.of(zone));

        FloodResponse response = floodService.checkFloodRisk(lat, lon);

        assertThat(response.riskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(response.insideFloodZone()).isTrue();
        assertThat(response.zoneName()).isEqualTo("Nullah Lai Flood Plain");
    }

    @Test
    @DisplayName("Should return MEDIUM risk when point is near a flood zone (within 500m)")
    void shouldReturnMediumRiskWhenNearZone() {
        double lat = 33.65;
        double lon = 73.05;
        FloodZone zone = createZone(2L, "Rawal Lake Overflow Zone", "MEDIUM");

        when(spatialQueryService.findContainingZones(lat, lon))
                .thenReturn(Collections.emptyList());
        when(spatialQueryService.findZonesWithinDistance(lat, lon))
                .thenReturn(List.of(zone));

        FloodResponse response = floodService.checkFloodRisk(lat, lon);

        assertThat(response.riskLevel()).isEqualTo(RiskLevel.MEDIUM);
        assertThat(response.insideFloodZone()).isFalse();
    }

    @Test
    @DisplayName("Should return LOW risk when point is outside all flood zones")
    void shouldReturnLowRiskWhenOutsideAllZones() {
        double lat = 33.71;
        double lon = 73.05;

        when(spatialQueryService.findContainingZones(lat, lon))
                .thenReturn(Collections.emptyList());
        when(spatialQueryService.findZonesWithinDistance(lat, lon))
                .thenReturn(Collections.emptyList());

        FloodResponse response = floodService.checkFloodRisk(lat, lon);

        assertThat(response.riskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(response.insideFloodZone()).isFalse();
        assertThat(response.zoneName()).isEqualTo("No Zone");
    }

    @Test
    @DisplayName("Should return distance 0 when point is inside flood zone for nearest query")
    void shouldReturnZeroDistanceWhenInsideZone() {
        double lat = 33.60;
        double lon = 73.06;
        FloodZone zone = createZone(1L, "Nullah Lai Flood Plain", "HIGH");

        when(spatialQueryService.findContainingZones(lat, lon))
                .thenReturn(List.of(zone));

        NearestZoneResponse response = floodService.findNearestFloodZone(lat, lon);

        assertThat(response.insideFloodZone()).isTrue();
        assertThat(response.nearestDistanceMeters()).isEqualTo(0.0);
        assertThat(response.riskLevel()).isEqualTo(RiskLevel.HIGH);
    }

    @Test
    @DisplayName("Should return nearest zone when point is outside all zones")
    void shouldReturnNearestZoneWhenOutside() {
        double lat = 33.65;
        double lon = 73.05;
        FloodZone zone = createZone(2L, "Rawal Lake Overflow Zone", "MEDIUM");

        when(spatialQueryService.findContainingZones(lat, lon))
                .thenReturn(Collections.emptyList());
        when(spatialQueryService.findNearestZone(lat, lon))
                .thenReturn(Optional.of(zone));

        NearestZoneResponse response = floodService.findNearestFloodZone(lat, lon);

        assertThat(response.insideFloodZone()).isFalse();
        assertThat(response.zoneName()).isEqualTo("Rawal Lake Overflow Zone");
    }
}
