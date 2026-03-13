-- 10: Add version tracking columns to flood_zones
ALTER TABLE flood_zones ADD (
    version_tag VARCHAR2(50),
    imported_at TIMESTAMP
);

COMMIT;
