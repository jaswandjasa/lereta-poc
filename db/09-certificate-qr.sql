-- 09: Add QR reference column to certificate_audit
ALTER TABLE certificate_audit ADD qr_reference VARCHAR2(500);

COMMIT;
