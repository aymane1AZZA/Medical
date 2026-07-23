CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(140) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(140) NOT NULL,
    role VARCHAR(40) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Identity and organization
CREATE TABLE IF NOT EXISTS organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(180) NOT NULL,
    organization_type VARCHAR(40) NOT NULL DEFAULT 'HOSPITAL',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    parent_id UUID REFERENCES locations(id),
    code VARCHAR(40) NOT NULL,
    name VARCHAR(180) NOT NULL,
    location_type VARCHAR(40) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (organization_id, code)
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS token_version INTEGER NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS failed_login_attempts INTEGER NOT NULL DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS locked_until TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ NOT NULL DEFAULT now();
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMPTZ;
ALTER TABLE users ADD COLUMN IF NOT EXISTS locale VARCHAR(12) NOT NULL DEFAULT 'fr-MA';
ALTER TABLE users ADD COLUMN IF NOT EXISTS timezone VARCHAR(64) NOT NULL DEFAULT 'Africa/Casablanca';

CREATE TABLE IF NOT EXISTS user_role_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL REFERENCES users(id),
    role_code VARCHAR(60) NOT NULL,
    organization_id UUID REFERENCES organizations(id),
    location_id UUID REFERENCES locations(id),
    valid_from TIMESTAMPTZ NOT NULL DEFAULT now(),
    valid_until TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (valid_until IS NULL OR valid_until > valid_from)
);
CREATE INDEX IF NOT EXISTS idx_user_roles_active ON user_role_assignments(user_id, active, valid_until);

CREATE TABLE IF NOT EXISTS care_teams (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    name VARCHAR(180) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS care_team_members (
    care_team_id UUID NOT NULL REFERENCES care_teams(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    member_role VARCHAR(60) NOT NULL,
    valid_from TIMESTAMPTZ NOT NULL DEFAULT now(),
    valid_until TIMESTAMPTZ,
    PRIMARY KEY (care_team_id, user_id, member_role, valid_from)
);

-- Patient master data
CREATE TABLE IF NOT EXISTS patients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    medical_record_number VARCHAR(64) NOT NULL UNIQUE,
    national_identifier VARCHAR(64),
    family_name VARCHAR(100) NOT NULL,
    given_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    administrative_gender VARCHAR(24) NOT NULL,
    blood_group VARCHAR(8),
    phone VARCHAR(32),
    email CITEXT,
    preferred_language VARCHAR(12) NOT NULL DEFAULT 'fr-MA',
    deceased_at TIMESTAMPTZ,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (birth_date <= CURRENT_DATE),
    CHECK (administrative_gender IN ('FEMALE','MALE','OTHER','UNKNOWN'))
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_patients_national_identifier ON patients(national_identifier) WHERE national_identifier IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_patients_name_birth ON patients(family_name, given_name, birth_date);
CREATE INDEX IF NOT EXISTS idx_patients_active ON patients(active);

CREATE TABLE IF NOT EXISTS patient_identifiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    system_uri VARCHAR(255) NOT NULL,
    value VARCHAR(128) NOT NULL,
    identifier_type VARCHAR(40) NOT NULL,
    valid_from DATE,
    valid_until DATE,
    UNIQUE(system_uri, value)
);

CREATE TABLE IF NOT EXISTS patient_contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    full_name VARCHAR(180) NOT NULL,
    relationship_code VARCHAR(60) NOT NULL,
    phone VARCHAR(32),
    email CITEXT,
    is_emergency_contact BOOLEAN NOT NULL DEFAULT FALSE,
    is_legal_representative BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS patient_allergies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    substance_code VARCHAR(100) NOT NULL,
    substance_display VARCHAR(180) NOT NULL,
    criticality VARCHAR(20) NOT NULL,
    reaction TEXT,
    clinical_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    recorded_by BIGINT REFERENCES users(id),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (criticality IN ('LOW','HIGH','UNABLE_TO_ASSESS'))
);
CREATE INDEX IF NOT EXISTS idx_patient_allergies_patient ON patient_allergies(patient_id, clinical_status);

CREATE TABLE IF NOT EXISTS patient_conditions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    code_system VARCHAR(120),
    code VARCHAR(80) NOT NULL,
    display VARCHAR(255) NOT NULL,
    clinical_status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
    onset_at TIMESTAMPTZ,
    abatement_at TIMESTAMPTZ,
    recorded_by BIGINT REFERENCES users(id),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS episodes_of_care (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    care_team_id UUID REFERENCES care_teams(id),
    status VARCHAR(24) NOT NULL,
    started_at TIMESTAMPTZ NOT NULL,
    ended_at TIMESTAMPTZ,
    managing_organization_id UUID REFERENCES organizations(id),
    version BIGINT NOT NULL DEFAULT 0,
    CHECK (ended_at IS NULL OR ended_at >= started_at)
);

-- Consent, documents and provenance
CREATE TABLE IF NOT EXISTS consents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    consent_type VARCHAR(60) NOT NULL,
    status VARCHAR(24) NOT NULL,
    scope_code VARCHAR(80) NOT NULL,
    granted_at TIMESTAMPTZ,
    valid_until TIMESTAMPTZ,
    withdrawn_at TIMESTAMPTZ,
    recorded_by BIGINT REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_consents_patient_status ON consents(patient_id, status, consent_type);

CREATE TABLE IF NOT EXISTS clinical_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patients(id),
    document_type VARCHAR(60) NOT NULL,
    title VARCHAR(255) NOT NULL,
    storage_key VARCHAR(500) NOT NULL UNIQUE,
    mime_type VARCHAR(120) NOT NULL,
    size_bytes BIGINT NOT NULL,
    checksum_sha256 CHAR(64) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'CURRENT',
    version_number INTEGER NOT NULL DEFAULT 1,
    supersedes_id UUID REFERENCES clinical_documents(id),
    authored_by BIGINT REFERENCES users(id),
    signed_by BIGINT REFERENCES users(id),
    signed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (size_bytes >= 0),
    CHECK (version_number > 0)
);

-- Versioned protocols and prescriptions
CREATE TABLE IF NOT EXISTS clinical_protocols (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(60) NOT NULL,
    name VARCHAR(255) NOT NULL,
    modality VARCHAR(60) NOT NULL,
    version_number INTEGER NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
    definition JSONB NOT NULL DEFAULT '{}'::jsonb,
    effective_from DATE,
    effective_until DATE,
    approved_by BIGINT REFERENCES users(id),
    approved_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(code, version_number),
    CHECK (version_number > 0)
);

CREATE TABLE IF NOT EXISTS apheresis_prescriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    episode_id UUID REFERENCES episodes_of_care(id),
    protocol_id UUID REFERENCES clinical_protocols(id),
    indication_code VARCHAR(100) NOT NULL,
    indication_display VARCHAR(255) NOT NULL,
    asfa_category VARCHAR(16),
    modality VARCHAR(60) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'DRAFT',
    priority VARCHAR(16) NOT NULL DEFAULT 'ROUTINE',
    sessions_planned INTEGER NOT NULL,
    frequency_text VARCHAR(255),
    target_volume_ml NUMERIC(12,2),
    replacement_fluid VARCHAR(100),
    anticoagulant VARCHAR(100),
    anticoagulant_ratio NUMERIC(8,3),
    calcium_prophylaxis TEXT,
    premedication TEXT,
    vascular_access_plan TEXT,
    clinical_instructions TEXT,
    prescribed_by BIGINT NOT NULL REFERENCES users(id),
    prescribed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    validated_by BIGINT REFERENCES users(id),
    validated_at TIMESTAMPTZ,
    cancelled_reason TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (sessions_planned > 0),
    CHECK (target_volume_ml IS NULL OR target_volume_ml > 0),
    CHECK (priority IN ('ROUTINE','URGENT','STAT')),
    CHECK (status IN ('DRAFT','SUBMITTED','VALIDATED','ACTIVE','COMPLETED','CANCELLED'))
);
CREATE INDEX IF NOT EXISTS idx_prescriptions_patient ON apheresis_prescriptions(patient_id, status, prescribed_at DESC);

CREATE TABLE IF NOT EXISTS prescription_requirements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prescription_id UUID NOT NULL REFERENCES apheresis_prescriptions(id) ON DELETE CASCADE,
    requirement_type VARCHAR(40) NOT NULL,
    code VARCHAR(100) NOT NULL,
    display VARCHAR(255) NOT NULL,
    timing VARCHAR(40),
    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    threshold_definition JSONB
);

-- Laboratory workflow
CREATE TABLE IF NOT EXISTS laboratory_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    prescription_id UUID REFERENCES apheresis_prescriptions(id),
    status VARCHAR(24) NOT NULL DEFAULT 'ORDERED',
    priority VARCHAR(16) NOT NULL DEFAULT 'ROUTINE',
    ordered_by BIGINT NOT NULL REFERENCES users(id),
    ordered_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    required_at TIMESTAMPTZ,
    clinical_context TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    CHECK (status IN ('ORDERED','COLLECTED','RECEIVED','IN_PROGRESS','PARTIAL','FINAL','CANCELLED'))
);

CREATE TABLE IF NOT EXISTS laboratory_order_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES laboratory_orders(id) ON DELETE CASCADE,
    loinc_code VARCHAR(32) NOT NULL,
    display VARCHAR(255) NOT NULL,
    specimen_type VARCHAR(80),
    status VARCHAR(24) NOT NULL DEFAULT 'ORDERED'
);

CREATE TABLE IF NOT EXISTS specimens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL REFERENCES laboratory_orders(id),
    accession_number VARCHAR(80) NOT NULL UNIQUE,
    barcode VARCHAR(120) NOT NULL UNIQUE,
    specimen_type VARCHAR(80) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'EXPECTED',
    collected_by BIGINT REFERENCES users(id),
    collected_at TIMESTAMPTZ,
    received_by BIGINT REFERENCES users(id),
    received_at TIMESTAMPTZ,
    rejection_reason TEXT
);

CREATE TABLE IF NOT EXISTS laboratory_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_item_id UUID NOT NULL REFERENCES laboratory_order_items(id),
    specimen_id UUID REFERENCES specimens(id),
    loinc_code VARCHAR(32) NOT NULL,
    value_numeric NUMERIC(20,6),
    value_text TEXT,
    unit_ucum VARCHAR(40),
    reference_low NUMERIC(20,6),
    reference_high NUMERIC(20,6),
    interpretation VARCHAR(20),
    status VARCHAR(24) NOT NULL DEFAULT 'PRELIMINARY',
    is_critical BOOLEAN NOT NULL DEFAULT FALSE,
    measured_at TIMESTAMPTZ NOT NULL,
    validated_by BIGINT REFERENCES users(id),
    validated_at TIMESTAMPTZ,
    amended_from_id UUID REFERENCES laboratory_results(id),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (value_numeric IS NOT NULL OR value_text IS NOT NULL)
);
CREATE INDEX IF NOT EXISTS idx_lab_results_item_time ON laboratory_results(order_item_id, measured_at DESC);

CREATE TABLE IF NOT EXISTS critical_result_acknowledgements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    result_id UUID NOT NULL REFERENCES laboratory_results(id),
    acknowledged_by BIGINT NOT NULL REFERENCES users(id),
    acknowledged_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    action_taken TEXT NOT NULL
);

-- Equipment and inventory
CREATE TABLE IF NOT EXISTS equipment (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    asset_number VARCHAR(80) NOT NULL UNIQUE,
    udi VARCHAR(255),
    manufacturer VARCHAR(120) NOT NULL,
    model VARCHAR(120) NOT NULL,
    serial_number VARCHAR(120) NOT NULL UNIQUE,
    equipment_type VARCHAR(60) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'AVAILABLE',
    location_id UUID REFERENCES locations(id),
    commissioned_on DATE,
    next_maintenance_at TIMESTAMPTZ,
    firmware_version VARCHAR(80),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (status IN ('AVAILABLE','RESERVED','IN_USE','MAINTENANCE','OUT_OF_SERVICE','RETIRED'))
);
CREATE INDEX IF NOT EXISTS idx_equipment_status_location ON equipment(status, location_id);

CREATE TABLE IF NOT EXISTS maintenance_work_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id UUID NOT NULL REFERENCES equipment(id),
    work_order_number VARCHAR(80) NOT NULL UNIQUE,
    maintenance_type VARCHAR(32) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'OPEN',
    priority VARCHAR(16) NOT NULL DEFAULT 'NORMAL',
    description TEXT NOT NULL,
    opened_by BIGINT NOT NULL REFERENCES users(id),
    assigned_to BIGINT REFERENCES users(id),
    opened_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    scheduled_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    completion_notes TEXT,
    next_due_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS inventory_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sku VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    item_type VARCHAR(60) NOT NULL,
    unit VARCHAR(32) NOT NULL,
    minimum_stock NUMERIC(14,3) NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS inventory_lots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL REFERENCES inventory_items(id),
    lot_number VARCHAR(120) NOT NULL,
    expires_on DATE,
    quantity_available NUMERIC(14,3) NOT NULL,
    location_id UUID REFERENCES locations(id),
    quarantined BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(item_id, lot_number, location_id),
    CHECK (quantity_available >= 0)
);

-- Scheduling and resource allocation
CREATE TABLE IF NOT EXISTS appointments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    prescription_id UUID REFERENCES apheresis_prescriptions(id),
    location_id UUID REFERENCES locations(id),
    equipment_id UUID REFERENCES equipment(id),
    status VARCHAR(24) NOT NULL DEFAULT 'PROPOSED',
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    reason TEXT,
    created_by BIGINT NOT NULL REFERENCES users(id),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (ends_at > starts_at),
    CHECK (status IN ('PROPOSED','BOOKED','ARRIVED','FULFILLED','CANCELLED','NO_SHOW'))
);
CREATE INDEX IF NOT EXISTS idx_appointments_time ON appointments(starts_at, ends_at, status);
CREATE INDEX IF NOT EXISTS idx_appointments_patient ON appointments(patient_id, starts_at DESC);

CREATE TABLE IF NOT EXISTS appointment_staff (
    appointment_id UUID NOT NULL REFERENCES appointments(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    participation_role VARCHAR(60) NOT NULL,
    PRIMARY KEY (appointment_id, user_id, participation_role)
);

-- Apheresis execution and monitoring
CREATE TABLE IF NOT EXISTS apheresis_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_number VARCHAR(80) NOT NULL UNIQUE,
    patient_id UUID NOT NULL REFERENCES patients(id),
    prescription_id UUID NOT NULL REFERENCES apheresis_prescriptions(id),
    appointment_id UUID REFERENCES appointments(id),
    equipment_id UUID REFERENCES equipment(id),
    location_id UUID REFERENCES locations(id),
    status VARCHAR(32) NOT NULL DEFAULT 'PLANNED',
    sequence_number INTEGER NOT NULL,
    planned_volume_ml NUMERIC(12,2),
    actual_processed_volume_ml NUMERIC(12,2),
    replacement_volume_ml NUMERIC(12,2),
    anticoagulant_volume_ml NUMERIC(12,2),
    fluid_balance_ml NUMERIC(12,2),
    vascular_access VARCHAR(120),
    started_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    validated_at TIMESTAMPTZ,
    validated_by BIGINT REFERENCES users(id),
    termination_reason TEXT,
    clinical_summary TEXT,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE(prescription_id, sequence_number),
    CHECK (sequence_number > 0),
    CHECK (status IN ('PLANNED','READY','IN_PROGRESS','PAUSED','COMPLETED','ABORTED','CANCELLED','VALIDATED')),
    CHECK (ended_at IS NULL OR started_at IS NULL OR ended_at >= started_at)
);
CREATE INDEX IF NOT EXISTS idx_sessions_patient_time ON apheresis_sessions(patient_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_sessions_active ON apheresis_sessions(status) WHERE status IN ('READY','IN_PROGRESS','PAUSED');

CREATE TABLE IF NOT EXISTS session_checklist_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES apheresis_sessions(id) ON DELETE CASCADE,
    item_code VARCHAR(80) NOT NULL,
    label VARCHAR(255) NOT NULL,
    mandatory BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(24) NOT NULL DEFAULT 'PENDING',
    completed_by BIGINT REFERENCES users(id),
    completed_at TIMESTAMPTZ,
    comment TEXT,
    UNIQUE(session_id, item_code)
);

CREATE TABLE IF NOT EXISTS session_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES apheresis_sessions(id),
    event_type VARCHAR(60) NOT NULL,
    from_status VARCHAR(32),
    to_status VARCHAR(32),
    reason TEXT,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    recorded_by BIGINT REFERENCES users(id),
    metadata JSONB NOT NULL DEFAULT '{}'::jsonb
);
CREATE INDEX IF NOT EXISTS idx_session_events_time ON session_events(session_id, occurred_at);

CREATE TABLE IF NOT EXISTS session_observations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES apheresis_sessions(id),
    observation_code VARCHAR(80) NOT NULL,
    code_system VARCHAR(120),
    value_numeric NUMERIC(20,6),
    value_text TEXT,
    unit_ucum VARCHAR(40),
    source VARCHAR(24) NOT NULL DEFAULT 'MANUAL',
    device_id UUID REFERENCES equipment(id),
    observed_at TIMESTAMPTZ NOT NULL,
    recorded_by BIGINT REFERENCES users(id),
    validation_status VARCHAR(24) NOT NULL DEFAULT 'FINAL',
    CHECK (value_numeric IS NOT NULL OR value_text IS NOT NULL),
    CHECK (source IN ('MANUAL','DEVICE','IMPORTED'))
);
CREATE INDEX IF NOT EXISTS idx_session_observations_time ON session_observations(session_id, observed_at, observation_code);

CREATE TABLE IF NOT EXISTS session_alarms (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES apheresis_sessions(id),
    device_id UUID REFERENCES equipment(id),
    alarm_code VARCHAR(80) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    message TEXT NOT NULL,
    raised_at TIMESTAMPTZ NOT NULL,
    acknowledged_at TIMESTAMPTZ,
    acknowledged_by BIGINT REFERENCES users(id),
    resolved_at TIMESTAMPTZ,
    action_taken TEXT
);
CREATE INDEX IF NOT EXISTS idx_session_alarms_open ON session_alarms(session_id, severity, raised_at) WHERE resolved_at IS NULL;

CREATE TABLE IF NOT EXISTS session_consumables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID NOT NULL REFERENCES apheresis_sessions(id),
    inventory_lot_id UUID NOT NULL REFERENCES inventory_lots(id),
    quantity NUMERIC(14,3) NOT NULL,
    recorded_by BIGINT NOT NULL REFERENCES users(id),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (quantity > 0)
);

CREATE TABLE IF NOT EXISTS biologic_products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id UUID REFERENCES apheresis_sessions(id),
    product_identifier VARCHAR(160) NOT NULL UNIQUE,
    product_type VARCHAR(80) NOT NULL,
    blood_group VARCHAR(8),
    volume_ml NUMERIC(12,2),
    expires_at TIMESTAMPTZ,
    disposition VARCHAR(40) NOT NULL,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Safety events and CAPA
CREATE TABLE IF NOT EXISTS incidents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    incident_number VARCHAR(80) NOT NULL UNIQUE,
    patient_id UUID REFERENCES patients(id),
    session_id UUID REFERENCES apheresis_sessions(id),
    equipment_id UUID REFERENCES equipment(id),
    category VARCHAR(60) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'OPEN',
    occurred_at TIMESTAMPTZ NOT NULL,
    detected_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    description TEXT NOT NULL,
    immediate_action TEXT,
    causality VARCHAR(40),
    reportable BOOLEAN NOT NULL DEFAULT FALSE,
    reported_by BIGINT NOT NULL REFERENCES users(id),
    assigned_to BIGINT REFERENCES users(id),
    closed_by BIGINT REFERENCES users(id),
    closed_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    CHECK (severity IN ('LOW','MODERATE','HIGH','CRITICAL')),
    CHECK (status IN ('OPEN','UNDER_REVIEW','ACTION_REQUIRED','CLOSED'))
);
CREATE INDEX IF NOT EXISTS idx_incidents_open ON incidents(status, severity, occurred_at DESC);

CREATE TABLE IF NOT EXISTS corrective_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    incident_id UUID NOT NULL REFERENCES incidents(id),
    action_type VARCHAR(24) NOT NULL,
    description TEXT NOT NULL,
    owner_id BIGINT NOT NULL REFERENCES users(id),
    due_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'OPEN',
    completed_at TIMESTAMPTZ,
    effectiveness_review TEXT
);

-- Work queues and notifications
CREATE TABLE IF NOT EXISTS clinical_tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    patient_id UUID REFERENCES patients(id),
    session_id UUID REFERENCES apheresis_sessions(id),
    task_type VARCHAR(60) NOT NULL,
    status VARCHAR(24) NOT NULL DEFAULT 'REQUESTED',
    priority VARCHAR(16) NOT NULL DEFAULT 'ROUTINE',
    description TEXT NOT NULL,
    requester_id BIGINT REFERENCES users(id),
    owner_id BIGINT REFERENCES users(id),
    owner_role VARCHAR(60),
    due_at TIMESTAMPTZ,
    accepted_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_tasks_queue ON clinical_tasks(owner_id, owner_role, status, priority, due_at);

CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recipient_id BIGINT NOT NULL REFERENCES users(id),
    patient_id UUID REFERENCES patients(id),
    notification_type VARCHAR(60) NOT NULL,
    severity VARCHAR(20) NOT NULL DEFAULT 'INFO',
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    action_url VARCHAR(500),
    requires_acknowledgement BOOLEAN NOT NULL DEFAULT FALSE,
    acknowledged_at TIMESTAMPTZ,
    read_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_notifications_recipient ON notifications(recipient_id, read_at, created_at DESC);

-- Authentication lifecycle
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token_hash CHAR(64) NOT NULL UNIQUE,
    family_id UUID NOT NULL,
    issued_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    revoked_at TIMESTAMPTZ,
    replaced_by_id UUID REFERENCES refresh_tokens(id),
    ip_address INET,
    user_agent VARCHAR(500)
);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_active ON refresh_tokens(user_id, expires_at) WHERE revoked_at IS NULL;

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS login_attempts (
    id BIGSERIAL PRIMARY KEY,
    identifier_hash CHAR(64) NOT NULL,
    user_id BIGINT REFERENCES users(id),
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(80),
    ip_address INET,
    user_agent VARCHAR(500),
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_login_attempts_identifier_time ON login_attempts(identifier_hash, occurred_at DESC);

-- Append-only security and clinical audit
CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_time TIMESTAMPTZ NOT NULL DEFAULT now(),
    event_type VARCHAR(80) NOT NULL,
    action VARCHAR(24) NOT NULL,
    outcome VARCHAR(24) NOT NULL,
    actor_user_id BIGINT REFERENCES users(id),
    actor_username VARCHAR(80),
    actor_roles TEXT,
    patient_id UUID REFERENCES patients(id),
    entity_type VARCHAR(100),
    entity_id VARCHAR(120),
    purpose_of_use VARCHAR(60),
    request_id VARCHAR(100),
    ip_address INET,
    user_agent VARCHAR(500),
    detail JSONB NOT NULL DEFAULT '{}'::jsonb,
    previous_hash CHAR(64),
    event_hash CHAR(64) NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_audit_patient_time ON audit_events(patient_id, event_time DESC);
CREATE INDEX IF NOT EXISTS idx_audit_actor_time ON audit_events(actor_user_id, event_time DESC);
CREATE INDEX IF NOT EXISTS idx_audit_entity ON audit_events(entity_type, entity_id, event_time DESC);

CREATE OR REPLACE FUNCTION prevent_audit_mutation() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'audit_events is append-only';
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS audit_events_no_update ON audit_events;
CREATE TRIGGER audit_events_no_update BEFORE UPDATE OR DELETE ON audit_events
FOR EACH ROW EXECUTE FUNCTION prevent_audit_mutation();

-- Initial organization. Production values can be updated through controlled administration.
INSERT INTO organizations(code, name, organization_type)
VALUES ('CHU-M6-MRK', 'CHU Mohammed VI Marrakech', 'HOSPITAL')
ON CONFLICT (code) DO NOTHING;
