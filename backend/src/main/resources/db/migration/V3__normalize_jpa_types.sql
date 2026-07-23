ALTER TABLE audit_events ALTER COLUMN event_hash TYPE VARCHAR(64);
ALTER TABLE audit_events ALTER COLUMN previous_hash TYPE VARCHAR(64);
ALTER TABLE refresh_tokens ALTER COLUMN token_hash TYPE VARCHAR(64);
