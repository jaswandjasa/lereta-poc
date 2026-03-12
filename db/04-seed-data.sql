-- ============================================================
-- Flood Zoning PoC — Sample Seed Data
-- Islamabad/Rawalpindi area flood zones + properties
-- ============================================================

-- ========== FLOOD ZONES ==========

-- Zone A: High risk — Nullah Lai flood plain (Rawalpindi)
INSERT INTO flood_zones (id, zone_name, risk_level, geom, geojson)
VALUES (
    1,
    'Nullah Lai Flood Plain',
    'HIGH',
    SDO_GEOMETRY(
        2003, 4326, NULL,
        SDO_ELEM_INFO_ARRAY(1, 1003, 1),
        SDO_ORDINATE_ARRAY(
            73.04, 33.58,
            73.08, 33.58,
            73.08, 33.62,
            73.04, 33.62,
            73.04, 33.58
        )
    ),
    '{"type":"Feature","properties":{"zone_name":"Nullah Lai Flood Plain","risk_level":"HIGH"},"geometry":{"type":"Polygon","coordinates":[[[73.04,33.58],[73.08,33.58],[73.08,33.62],[73.04,33.62],[73.04,33.58]]]}}'
);

-- Zone B: Medium risk — Rawal Lake overflow zone
INSERT INTO flood_zones (id, zone_name, risk_level, geom, geojson)
VALUES (
    2,
    'Rawal Lake Overflow Zone',
    'MEDIUM',
    SDO_GEOMETRY(
        2003, 4326, NULL,
        SDO_ELEM_INFO_ARRAY(1, 1003, 1),
        SDO_ORDINATE_ARRAY(
            73.10, 33.68,
            73.15, 33.68,
            73.15, 33.72,
            73.10, 33.72,
            73.10, 33.68
        )
    ),
    '{"type":"Feature","properties":{"zone_name":"Rawal Lake Overflow Zone","risk_level":"MEDIUM"},"geometry":{"type":"Polygon","coordinates":[[[73.10,33.68],[73.15,33.68],[73.15,33.72],[73.10,33.72],[73.10,33.68]]]}}'
);

-- Zone C: High risk — Margalla foothills flash flood corridor
INSERT INTO flood_zones (id, zone_name, risk_level, geom, geojson)
VALUES (
    3,
    'Margalla Flash Flood Corridor',
    'HIGH',
    SDO_GEOMETRY(
        2003, 4326, NULL,
        SDO_ELEM_INFO_ARRAY(1, 1003, 1),
        SDO_ORDINATE_ARRAY(
            73.02, 33.73,
            73.09, 33.73,
            73.09, 33.76,
            73.02, 33.76,
            73.02, 33.73
        )
    ),
    '{"type":"Feature","properties":{"zone_name":"Margalla Flash Flood Corridor","risk_level":"HIGH"},"geometry":{"type":"Polygon","coordinates":[[[73.02,33.73],[73.09,33.73],[73.09,33.76],[73.02,33.76],[73.02,33.73]]]}}'
);

-- Zone D: Medium risk — Soan River buffer
INSERT INTO flood_zones (id, zone_name, risk_level, geom, geojson)
VALUES (
    4,
    'Soan River Buffer Zone',
    'MEDIUM',
    SDO_GEOMETRY(
        2003, 4326, NULL,
        SDO_ELEM_INFO_ARRAY(1, 1003, 1),
        SDO_ORDINATE_ARRAY(
            72.95, 33.55,
            73.05, 33.55,
            73.05, 33.58,
            72.95, 33.58,
            72.95, 33.55
        )
    ),
    '{"type":"Feature","properties":{"zone_name":"Soan River Buffer Zone","risk_level":"MEDIUM"},"geometry":{"type":"Polygon","coordinates":[[[72.95,33.55],[73.05,33.55],[73.05,33.58],[72.95,33.58],[72.95,33.55]]]}}'
);

-- ========== PROPERTIES ==========

-- Inside Nullah Lai flood zone (HIGH risk)
INSERT INTO properties (id, property_name, latitude, longitude, geom)
VALUES (
    1,
    'Riverside Heights Plot 7 — Rawalpindi',
    33.60, 73.06,
    SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(73.06, 33.60, NULL), NULL, NULL)
);

-- Inside Rawal Lake overflow zone (MEDIUM risk)
INSERT INTO properties (id, property_name, latitude, longitude, geom)
VALUES (
    2,
    'Lake View Residency — Bani Gala',
    33.70, 73.12,
    SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(73.12, 33.70, NULL), NULL, NULL)
);

-- Outside all zones (LOW risk)
INSERT INTO properties (id, property_name, latitude, longitude, geom)
VALUES (
    3,
    'Blue Area Commercial Plot 14 — Islamabad',
    33.71, 73.05,
    SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(73.05, 33.71, NULL), NULL, NULL)
);

-- Near Margalla flash flood corridor
INSERT INTO properties (id, property_name, latitude, longitude, geom)
VALUES (
    4,
    'Margalla Enclave Villa 9 — E-7',
    33.735, 73.06,
    SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(73.06, 33.735, NULL), NULL, NULL)
);

-- Inside Soan River buffer zone
INSERT INTO properties (id, property_name, latitude, longitude, geom)
VALUES (
    5,
    'Garden City Estate — Kahuta Road',
    33.565, 73.00,
    SDO_GEOMETRY(2001, 4326, SDO_POINT_TYPE(73.00, 33.565, NULL), NULL, NULL)
);

COMMIT;
