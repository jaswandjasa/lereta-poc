package com.estate.floodzoning.service;

import com.estate.floodzoning.dto.BufferResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BufferService {

    private final JdbcTemplate jdbcTemplate;

    private static final int[] BUFFER_DISTANCES = {500, 1500, 2500};
    private static final String[] BUFFER_LABELS = {"HIGH", "MEDIUM", "LOW"};

    public BufferResponse generateBuffer(double lat, double lon) {
        log.info("Generating multi-layer buffers (500m/1500m/2500m) for lat={}, lon={}", lat, lon);

        String sql = """
                SELECT SDO_UTIL.TO_GEOJSON(
                    SDO_GEOM.SDO_BUFFER(fz.geom, ?, 0.005, 'unit=METER')
                )
                FROM flood_zones fz
                WHERE SDO_NN(
                    fz.geom,
                    SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(?, ?, NULL), NULL, NULL),
                    'sdo_num_res=1'
                ) = 'TRUE'
                """;

        String[] features = new String[3];
        for (int i = 0; i < BUFFER_DISTANCES.length; i++) {
            String geojson = jdbcTemplate.queryForObject(sql, String.class, BUFFER_DISTANCES[i], lon, lat);
            if (geojson != null && !geojson.isBlank()) {
                features[i] = String.format(
                        "{\"type\":\"Feature\",\"properties\":{\"buffer_distance_m\":%d,\"risk_level\":\"%s\"},\"geometry\":%s}",
                        BUFFER_DISTANCES[i], BUFFER_LABELS[i], geojson);
            }
        }

        if (features[0] == null && features[1] == null && features[2] == null) {
            log.warn("No buffers generated for lat={}, lon={}", lat, lon);
            return new BufferResponse(null, null, null, null);
        }

        log.debug("Multi-layer buffers generated for lat={}, lon={}", lat, lon);
        // geojson field kept as the 500m buffer for backward compatibility
        return new BufferResponse(features[0], features[0], features[1], features[2]);
    }
}
