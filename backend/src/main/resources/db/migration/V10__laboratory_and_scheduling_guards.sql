CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE appointments
    ADD CONSTRAINT appointments_equipment_no_overlap
    EXCLUDE USING gist (
        equipment_id WITH =,
        tstzrange(starts_at, ends_at, '[)') WITH &&
    ) WHERE (equipment_id IS NOT NULL AND status IN ('PROPOSED','BOOKED','ARRIVED'));

ALTER TABLE appointments
    ADD CONSTRAINT appointments_patient_no_overlap
    EXCLUDE USING gist (
        patient_id WITH =,
        tstzrange(starts_at, ends_at, '[)') WITH &&
    ) WHERE (status IN ('PROPOSED','BOOKED','ARRIVED'));

CREATE INDEX idx_appointment_staff_user ON appointment_staff(user_id, appointment_id);
CREATE INDEX idx_specimens_order_status ON specimens(order_id, status);
CREATE INDEX idx_critical_ack_result ON critical_result_acknowledgements(result_id, acknowledged_at DESC);
