ALTER TABLE refresh_tokens ALTER COLUMN ip_address TYPE VARCHAR(64) USING ip_address::text;
ALTER TABLE audit_events ALTER COLUMN ip_address TYPE VARCHAR(64) USING ip_address::text;
