-- ============================================================
-- Flood Zoning PoC — Flood Certificate Audit Tables
-- ============================================================

CREATE TABLE certificate_audit (
    id                      NUMBER          PRIMARY KEY,
    certificate_number      VARCHAR2(50)    NOT NULL UNIQUE,
    property_id             NUMBER          NOT NULL REFERENCES properties(id),
    property_name_snapshot  VARCHAR2(200),
    latitude_snapshot       NUMBER,
    longitude_snapshot      NUMBER,
    risk_level              VARCHAR2(20),
    zone_name               VARCHAR2(100),
    pdf_hash                VARCHAR2(128),
    generated_at            TIMESTAMP,
    generated_by            VARCHAR2(100)
);

CREATE SEQUENCE certificate_audit_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE certificate_seq START WITH 1 INCREMENT BY 1;

CREATE INDEX idx_ca_property_id ON certificate_audit(property_id);
CREATE INDEX idx_ca_cert_number ON certificate_audit(certificate_number);

COMMIT;
