-- ============================================================
-- Flood Zoning PoC — Spatial Metadata Registration
-- Must be run BEFORE creating spatial indexes
-- ============================================================

-- Register geometry metadata for properties table
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES (
    'PROPERTIES',
    'GEOM',
    SDO_DIM_ARRAY(
        SDO_DIM_ELEMENT('X', -180, 180, 0.005),
        SDO_DIM_ELEMENT('Y', -90, 90, 0.005)
    ),
    4326
);

-- Register geometry metadata for flood_zones table
INSERT INTO USER_SDO_GEOM_METADATA (TABLE_NAME, COLUMN_NAME, DIMINFO, SRID)
VALUES (
    'FLOOD_ZONES',
    'GEOM',
    SDO_DIM_ARRAY(
        SDO_DIM_ELEMENT('X', -180, 180, 0.005),
        SDO_DIM_ELEMENT('Y', -90, 90, 0.005)
    ),
    4326
);

COMMIT;
