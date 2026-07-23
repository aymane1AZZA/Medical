-- Equipment lifecycle and immutable inventory ledger.
ALTER TABLE maintenance_work_orders
    ADD CONSTRAINT chk_maintenance_status CHECK (status IN ('OPEN','PLANNED','IN_PROGRESS','COMPLETED','CANCELLED')),
    ADD CONSTRAINT chk_maintenance_priority CHECK (priority IN ('LOW','NORMAL','HIGH','CRITICAL'));

CREATE INDEX IF NOT EXISTS idx_maintenance_equipment_status ON maintenance_work_orders(equipment_id, status, scheduled_at);
CREATE INDEX IF NOT EXISTS idx_inventory_lots_expiry ON inventory_lots(expires_on, quarantined) WHERE quantity_available > 0;

CREATE TABLE IF NOT EXISTS inventory_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lot_id UUID NOT NULL REFERENCES inventory_lots(id),
    movement_type VARCHAR(24) NOT NULL,
    quantity NUMERIC(14,3) NOT NULL,
    quantity_before NUMERIC(14,3) NOT NULL,
    quantity_after NUMERIC(14,3) NOT NULL,
    reference_type VARCHAR(60),
    reference_id UUID,
    reason TEXT,
    recorded_by BIGINT NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (movement_type IN ('RECEIPT','CONSUMPTION','ADJUSTMENT','QUARANTINE','RELEASE','DISPOSAL')),
    CHECK (quantity > 0),
    CHECK (quantity_after >= 0)
);
CREATE INDEX IF NOT EXISTS idx_inventory_movements_lot ON inventory_movements(lot_id, recorded_at DESC);

-- Document governance.
ALTER TABLE clinical_documents
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS confidentiality VARCHAR(24) NOT NULL DEFAULT 'CLINICAL',
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS deleted_by BIGINT REFERENCES users(id),
    ADD CONSTRAINT chk_document_status CHECK (status IN ('CURRENT','SUPERSEDED','SIGNED','WITHDRAWN')),
    ADD CONSTRAINT chk_document_confidentiality CHECK (confidentiality IN ('CLINICAL','RESTRICTED','PATIENT_VISIBLE'));
CREATE INDEX IF NOT EXISTS idx_documents_patient_current ON clinical_documents(patient_id, document_type, created_at DESC) WHERE deleted_at IS NULL;

-- Human-governed clinical assistance. Outputs are drafts, never autonomous orders.
CREATE TABLE IF NOT EXISTS ai_assistance_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patients(id),
    session_id UUID REFERENCES apheresis_sessions(id),
    assistance_type VARCHAR(40) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    clinician_input TEXT,
    generated_output TEXT NOT NULL,
    risk_flags JSONB NOT NULL DEFAULT '[]'::jsonb,
    source_facts JSONB NOT NULL DEFAULT '{}'::jsonb,
    model_provider VARCHAR(60) NOT NULL DEFAULT 'RULE_ENGINE',
    model_name VARCHAR(100) NOT NULL DEFAULT 'CLINICAL_ASSIST_V1',
    status VARCHAR(24) NOT NULL DEFAULT 'GENERATED',
    created_by BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMPTZ,
    review_note TEXT,
    CHECK (assistance_type IN ('HANDOFF_DRAFT','SESSION_SUMMARY','DATA_QUALITY','PATIENT_EXPLANATION')),
    CHECK (status IN ('GENERATED','ACCEPTED','REJECTED')),
    CHECK (patient_id IS NOT NULL OR session_id IS NOT NULL)
);
CREATE INDEX IF NOT EXISTS idx_ai_assistance_patient ON ai_assistance_requests(patient_id, created_at DESC);

-- Patient-entered outcomes and questionnaires.
CREATE TABLE IF NOT EXISTS patient_reported_outcomes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    session_id UUID REFERENCES apheresis_sessions(id),
    questionnaire_code VARCHAR(80) NOT NULL,
    response JSONB NOT NULL,
    score NUMERIC(12,3),
    status VARCHAR(24) NOT NULL DEFAULT 'SUBMITTED',
    submitted_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    reviewed_by BIGINT REFERENCES users(id),
    reviewed_at TIMESTAMPTZ,
    CHECK (status IN ('SUBMITTED','REVIEWED','FLAGGED'))
);
CREATE INDEX IF NOT EXISTS idx_patient_outcomes_patient ON patient_reported_outcomes(patient_id, submitted_at DESC);

-- Notification preferences remain overridable by critical clinical alerts.
CREATE TABLE IF NOT EXISTS notification_preferences (
    user_id BIGINT PRIMARY KEY REFERENCES users(id),
    in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sms_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    quiet_hours_start TIME,
    quiet_hours_end TIME,
    timezone VARCHAR(60) NOT NULL DEFAULT 'Africa/Casablanca',
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
