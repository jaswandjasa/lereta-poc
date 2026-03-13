-- 08: Property Risk History Timeline
-- Stores every monitoring transition for lender-grade audit trail

CREATE TABLE property_risk_history (
    id              NUMBER PRIMARY KEY,
    property_id     NUMBER NOT NULL REFERENCES properties(id),
    old_risk        VARCHAR2(20),
    new_risk        VARCHAR2(20),
    old_zone        VARCHAR2(100),
    new_zone        VARCHAR2(100),
    changed_at      TIMESTAMP DEFAULT SYSTIMESTAMP,
    trigger_source  VARCHAR2(50) DEFAULT 'MONITORING'
);

CREATE SEQUENCE property_risk_history_seq START WITH 1 INCREMENT BY 1;

CREATE INDEX idx_risk_history_property ON property_risk_history(property_id);
CREATE INDEX idx_risk_history_changed  ON property_risk_history(changed_at);

COMMIT;
