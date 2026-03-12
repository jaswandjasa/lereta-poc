-- ============================================================
-- Flood Zoning PoC — Table Creation
-- Oracle 19c with Spatial (SDO_GEOMETRY)
-- ============================================================

-- Properties table
CREATE TABLE properties (
    id              NUMBER          PRIMARY KEY,
    property_name   VARCHAR2(200)   NOT NULL,
    latitude        NUMBER          NOT NULL,
    longitude       NUMBER          NOT NULL,
    geom            SDO_GEOMETRY
);

-- Flood zones table
CREATE TABLE flood_zones (
    id              NUMBER          PRIMARY KEY,
    zone_name       VARCHAR2(100)   NOT NULL,
    risk_level      VARCHAR2(20)    NOT NULL,
    geom            SDO_GEOMETRY,
    geojson         CLOB
);

-- Sequences for auto-incrementing IDs
CREATE SEQUENCE properties_seq START WITH 100 INCREMENT BY 1;
CREATE SEQUENCE flood_zones_seq START WITH 100 INCREMENT BY 1;
