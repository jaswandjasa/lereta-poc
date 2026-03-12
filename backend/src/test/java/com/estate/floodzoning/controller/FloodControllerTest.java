package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.FloodResponse;
import com.estate.floodzoning.dto.NearestZoneResponse;
import com.estate.floodzoning.enums.RiskLevel;
import com.estate.floodzoning.service.FloodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FloodControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FloodService floodService;

    @InjectMocks
    private FloodController floodController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(floodController).build();
    }

    @Test
    @DisplayName("GET /api/flood/check should return flood risk response")
    void checkFloodRisk_shouldReturnRiskResponse() throws Exception {
        FloodResponse response = new FloodResponse("Nullah Lai Flood Plain", RiskLevel.HIGH, true);
        when(floodService.checkFloodRisk(anyDouble(), anyDouble())).thenReturn(response);

        mockMvc.perform(get("/api/flood/check")
                        .param("lat", "33.60")
                        .param("lon", "73.06"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.insideFloodZone").value(true))
                .andExpect(jsonPath("$.zoneName").value("Nullah Lai Flood Plain"));
    }

    @Test
    @DisplayName("GET /api/flood/nearest should return nearest zone response")
    void findNearestZone_shouldReturnNearestResponse() throws Exception {
        NearestZoneResponse response = new NearestZoneResponse(
                "Rawal Lake Overflow Zone", RiskLevel.MEDIUM, false, 250.0);
        when(floodService.findNearestFloodZone(anyDouble(), anyDouble())).thenReturn(response);

        mockMvc.perform(get("/api/flood/nearest")
                        .param("lat", "33.65")
                        .param("lon", "73.05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("MEDIUM"))
                .andExpect(jsonPath("$.insideFloodZone").value(false))
                .andExpect(jsonPath("$.nearestDistanceMeters").value(250.0));
    }

    @Test
    @DisplayName("GET /api/flood/check should return 400 for invalid latitude")
    void checkFloodRisk_shouldRejectInvalidLatitude() throws Exception {
        mockMvc.perform(get("/api/flood/check")
                        .param("lat", "999")
                        .param("lon", "73.06"))
                .andExpect(status().isBadRequest());
    }
}
