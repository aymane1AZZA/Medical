DROP TRIGGER IF EXISTS audit_events_no_update ON audit_events;

ALTER TABLE audit_events ADD COLUMN chain_position BIGINT;
ALTER TABLE audit_events ADD COLUMN hash_version INTEGER NOT NULL DEFAULT 0;

WITH RECURSIVE ordered AS (
    SELECT id, event_hash, 1::BIGINT AS position
    FROM audit_events
    WHERE previous_hash IS NULL
    UNION ALL
    SELECT event.id, event.event_hash, ordered.position + 1
    FROM audit_events event
    JOIN ordered ON event.previous_hash = ordered.event_hash
)
UPDATE audit_events event
SET chain_position = ordered.position
FROM ordered
WHERE event.id = ordered.id;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM audit_events WHERE chain_position IS NULL) THEN
        RAISE EXCEPTION 'Existing audit chain is disconnected';
    END IF;
END $$;

ALTER TABLE audit_events ALTER COLUMN chain_position SET NOT NULL;
CREATE UNIQUE INDEX uq_audit_events_chain_position ON audit_events(chain_position);

CREATE TABLE audit_chain_heads (
    id SMALLINT PRIMARY KEY,
    last_position BIGINT NOT NULL,
    last_hash VARCHAR(64),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (id = 1),
    CHECK (last_position >= 0)
);

INSERT INTO audit_chain_heads(id, last_position, last_hash)
SELECT 1,
       COALESCE(MAX(chain_position), 0),
       (SELECT event_hash FROM audit_events ORDER BY chain_position DESC LIMIT 1)
FROM audit_events;

CREATE TRIGGER audit_events_no_update BEFORE UPDATE OR DELETE ON audit_events
FOR EACH ROW EXECUTE FUNCTION prevent_audit_mutation();
