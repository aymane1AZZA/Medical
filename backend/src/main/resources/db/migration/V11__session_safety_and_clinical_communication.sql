-- Operational session safety, durable collaboration and configurable surveillance.
ALTER TABLE session_alarms
    ADD COLUMN IF NOT EXISTS source VARCHAR(24) NOT NULL DEFAULT 'MANUAL',
    ADD COLUMN IF NOT EXISTS escalated_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS escalated_to BIGINT REFERENCES users(id);

ALTER TABLE incidents
    ADD COLUMN IF NOT EXISTS root_cause TEXT,
    ADD COLUMN IF NOT EXISTS closure_review TEXT;

CREATE INDEX IF NOT EXISTS idx_session_consumables_lot ON session_consumables(inventory_lot_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_corrective_actions_open ON corrective_actions(status, due_at) WHERE status <> 'COMPLETED';

CREATE TABLE IF NOT EXISTS observation_alert_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    observation_code VARCHAR(80) NOT NULL UNIQUE,
    display VARCHAR(160) NOT NULL,
    lower_limit NUMERIC(20,6),
    upper_limit NUMERIC(20,6),
    severity VARCHAR(20) NOT NULL DEFAULT 'HIGH',
    unit_ucum VARCHAR(40),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    CHECK (lower_limit IS NOT NULL OR upper_limit IS NOT NULL),
    CHECK (lower_limit IS NULL OR upper_limit IS NULL OR lower_limit < upper_limit),
    CHECK (severity IN ('LOW','MODERATE','HIGH','CRITICAL'))
);

INSERT INTO observation_alert_rules(observation_code, display, lower_limit, upper_limit, severity, unit_ucum)
VALUES
    ('HEART_RATE', 'Frequence cardiaque', 45, 130, 'HIGH', '/min'),
    ('SYSTOLIC_BP', 'Pression arterielle systolique', 80, 180, 'CRITICAL', 'mm[Hg]'),
    ('SPO2', 'Saturation en oxygene', 90, NULL, 'CRITICAL', '%'),
    ('TEMPERATURE', 'Temperature corporelle', 35, 39, 'HIGH', 'Cel')
ON CONFLICT (observation_code) DO NOTHING;

CREATE TABLE IF NOT EXISTS clinical_threads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patients(id),
    session_id UUID REFERENCES apheresis_sessions(id),
    subject VARCHAR(255) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(16) NOT NULL DEFAULT 'ROUTINE',
    created_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (patient_id IS NOT NULL OR session_id IS NOT NULL),
    CHECK (status IN ('OPEN','RESOLVED','ARCHIVED')),
    CHECK (priority IN ('ROUTINE','URGENT','STAT'))
);

CREATE TABLE IF NOT EXISTS clinical_thread_participants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    thread_id UUID NOT NULL REFERENCES clinical_threads(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    participant_role VARCHAR(60),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_read_at TIMESTAMPTZ,
    muted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(thread_id, user_id)
);

CREATE TABLE IF NOT EXISTS clinical_messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    thread_id UUID NOT NULL REFERENCES clinical_threads(id) ON DELETE CASCADE,
    sender_id BIGINT NOT NULL REFERENCES users(id),
    reply_to_id UUID REFERENCES clinical_messages(id),
    message_type VARCHAR(24) NOT NULL DEFAULT 'TEXT',
    urgency VARCHAR(16) NOT NULL DEFAULT 'ROUTINE',
    body TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    edited_at TIMESTAMPTZ,
    deleted_at TIMESTAMPTZ,
    CHECK (message_type IN ('TEXT','HANDOFF','ALERT','DECISION')),
    CHECK (urgency IN ('ROUTINE','URGENT','STAT'))
);

CREATE INDEX IF NOT EXISTS idx_thread_participants_user ON clinical_thread_participants(user_id, thread_id);
CREATE INDEX IF NOT EXISTS idx_threads_patient ON clinical_threads(patient_id, updated_at DESC);
CREATE INDEX IF NOT EXISTS idx_messages_thread_time ON clinical_messages(thread_id, created_at);

CREATE TABLE IF NOT EXISTS event_outbox (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(80) NOT NULL,
    aggregate_id UUID,
    event_type VARCHAR(100) NOT NULL,
    recipient_id BIGINT REFERENCES users(id),
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_event_outbox_pending ON event_outbox(occurred_at) WHERE published_at IS NULL;
