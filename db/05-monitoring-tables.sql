-- ============================================================
-- Flood Zoning PoC — Life-of-Loan Monitoring Tables
-- ============================================================

CREATE TABLE property_monitoring (
    id                 NUMBER          PRIMARY KEY,
    property_id        NUMBER          NOT NULL REFERENCES properties(id),
    last_zone          VARCHAR2(100),
    current_zone       VARCHAR2(100),
    last_risk          VARCHAR2(20),
    current_risk       VARCHAR2(20),
    last_checked       TIMESTAMP,
    status_changed     NUMBER(1)       DEFAULT 0,
    monitoring_enabled NUMBER(1)       DEFAULT 1
);

CREATE SEQUENCE property_monitoring_seq START WITH 1 INCREMENT BY 1;

CREATE INDEX idx_pm_property_id ON property_monitoring(property_id);

-- Seed: enroll all existing properties for monitoring
INSERT INTO property_monitoring (id, property_id, monitoring_enabled)
VALUES (property_monitoring_seq.NEXTVAL, 1, 1);

INSERT INTO property_monitoring (id, property_id, monitoring_enabled)
VALUES (property_monitoring_seq.NEXTVAL, 2, 1);

INSERT INTO property_monitoring (id, property_id, monitoring_enabled)
VALUES (property_monitoring_seq.NEXTVAL, 3, 1);

INSERT INTO property_monitoring (id, property_id, monitoring_enabled)
VALUES (property_monitoring_seq.NEXTVAL, 4, 1);

INSERT INTO property_monitoring (id, property_id, monitoring_enabled)
VALUES (property_monitoring_seq.NEXTVAL, 5, 1);

COMMIT;
