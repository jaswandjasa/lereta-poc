package com.estate.floodzoning.controller;

import com.estate.floodzoning.dto.ZoneVersionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneVersionController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/versions")
    public ResponseEntity<List<ZoneVersionDto>> getVersions() {
        String sql = """
                SELECT version_tag, zone_count, imported_at FROM (
                    SELECT version_tag, COUNT(*) AS zone_count, MIN(imported_at) AS imported_at
                    FROM flood_zones
                    WHERE version_tag IS NOT NULL
                    GROUP BY version_tag
                ) ORDER BY imported_at DESC
                """;

        List<ZoneVersionDto> versions = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp ts = rs.getTimestamp("imported_at");
            return new ZoneVersionDto(
                    rs.getString("version_tag"),
                    rs.getLong("zone_count"),
                    ts != null ? ts.toLocalDateTime() : null);
        });

        return ResponseEntity.ok(versions);
    }
}
