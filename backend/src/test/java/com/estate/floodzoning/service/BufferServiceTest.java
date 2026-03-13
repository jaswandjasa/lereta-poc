package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.BufferResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BufferServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private BufferService bufferService;

    @Test
    @DisplayName("Should return GeoJSON Feature wrapping buffer polygon")
    void shouldReturnBufferFeature() {
        String rawGeometry = "{\"type\":\"Polygon\",\"coordinates\":[[[73.0,33.5],[73.1,33.5],[73.1,33.6],[73.0,33.6],[73.0,33.5]]]}";

        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(73.06), eq(33.60)))
                .thenReturn(rawGeometry);

        BufferResponse result = bufferService.generateBuffer(33.60, 73.06);

        assertThat(result.geojson()).isNotNull();
        assertThat(result.geojson()).contains("\"type\":\"Feature\"");
        assertThat(result.geojson()).contains("\"buffer_distance_m\":500");
        assertThat(result.geojson()).contains("\"type\":\"Polygon\"");
    }

    @Test
    @DisplayName("Should return null geojson when no buffer generated")
    void shouldReturnNullWhenNoBuffer() {
        when(jdbcTemplate.queryForObject(anyString(), eq(String.class), eq(73.06), eq(33.60)))
                .thenReturn(null);

        BufferResponse result = bufferService.generateBuffer(33.60, 73.06);

        assertThat(result.geojson()).isNull();
    }
}
