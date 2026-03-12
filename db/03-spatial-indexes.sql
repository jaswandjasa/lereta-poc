-- ============================================================
-- Flood Zoning PoC — Spatial Index Creation
-- Run AFTER spatial metadata registration
-- ============================================================

-- Spatial index on properties geometry
CREATE INDEX properties_spatial_idx
ON properties(geom)
INDEXTYPE IS MDSYS.SPATIAL_INDEX;

-- Spatial index on flood_zones geometry
CREATE INDEX flood_zones_spatial_idx
ON flood_zones(geom)
INDEXTYPE IS MDSYS.SPATIAL_INDEX;
