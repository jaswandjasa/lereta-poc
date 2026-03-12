-- ============================================================
-- Flood Zoning PoC — Flood Alert Tables
-- ============================================================

CREATE TABLE flood_alerts (
    id                NUMBER          PRIMARY KEY,
    property_id       NUMBER          NOT NULL REFERENCES properties(id),
    old_risk          VARCHAR2(20),
    new_risk          VARCHAR2(20),
    old_zone          VARCHAR2(100),
    new_zone          VARCHAR2(100),
    alert_type        VARCHAR2(30)    NOT NULL,
    created_at        TIMESTAMP       DEFAULT SYSTIMESTAMP,
    acknowledged      NUMBER(1)       DEFAULT 0,
    acknowledged_at   TIMESTAMP
);

CREATE SEQUENCE flood_alerts_seq START WITH 1 INCREMENT BY 1;

CREATE INDEX idx_fa_property_id ON flood_alerts(property_id);
CREATE INDEX idx_fa_acknowledged ON flood_alerts(acknowledged);
CREATE INDEX idx_fa_created_at ON flood_alerts(created_at);

COMMIT;
