package com.estate.floodzoning.repository;

import com.estate.floodzoning.domain.FloodZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FloodZoneRepository extends JpaRepository<FloodZone, Long> {

    @Query(value = """
            SELECT fz.id, fz.zone_name, fz.risk_level, fz.geojson
            FROM flood_zones fz
            WHERE SDO_CONTAINS(
                fz.geom,
                SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(:lon, :lat, NULL), NULL, NULL)
            ) = 'TRUE'
            """, nativeQuery = true)
    List<FloodZone> findContainingZones(@Param("lon") double lon, @Param("lat") double lat);

    @Query(value = """
            SELECT fz.id, fz.zone_name, fz.risk_level, fz.geojson
            FROM flood_zones fz
            WHERE SDO_WITHIN_DISTANCE(
                fz.geom,
                SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(:lon, :lat, NULL), NULL, NULL),
                'distance=500 unit=METER'
            ) = 'TRUE'
            """, nativeQuery = true)
    List<FloodZone> findZonesWithinDistance(@Param("lon") double lon, @Param("lat") double lat);

    @Query(value = """
            SELECT fz.id, fz.zone_name, fz.risk_level, fz.geojson
            FROM flood_zones fz
            WHERE SDO_NN(
                fz.geom,
                SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(:lon, :lat, NULL), NULL, NULL),
                'sdo_num_res=1'
            ) = 'TRUE'
            """, nativeQuery = true)
    List<FloodZone> findNearestZone(@Param("lon") double lon, @Param("lat") double lat);
}
