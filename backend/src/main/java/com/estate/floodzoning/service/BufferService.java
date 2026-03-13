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

    public BufferResponse generateBuffer(double lat, double lon) {
        log.info("Generating 500m buffer for lat={}, lon={}", lat, lon);

        String sql = """
                SELECT SDO_UTIL.TO_GEOJSON(
                    SDO_GEOM.SDO_BUFFER(fz.geom, 500, 0.005, 'unit=METER')
                )
                FROM flood_zones fz
                WHERE SDO_NN(
                    fz.geom,
                    SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(?, ?, NULL), NULL, NULL),
                    'sdo_num_res=1'
                ) = 'TRUE'
                """;

        String geojson = jdbcTemplate.queryForObject(sql, String.class, lon, lat);

        if (geojson == null || geojson.isBlank()) {
            log.warn("No buffer generated for lat={}, lon={}", lat, lon);
            return new BufferResponse(null);
        }

        // Wrap raw geometry in a GeoJSON Feature
        String feature = String.format(
                "{\"type\":\"Feature\",\"properties\":{\"buffer_distance_m\":500},\"geometry\":%s}",
                geojson);

        log.debug("Buffer generated: {} chars", feature.length());
        return new BufferResponse(feature);
    }
}
