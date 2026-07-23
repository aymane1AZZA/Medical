ALTER TABLE patient_contacts ALTER COLUMN email TYPE VARCHAR(255) USING email::text;

CREATE INDEX idx_patient_identifiers_patient ON patient_identifiers(patient_id);
CREATE INDEX idx_patient_contacts_patient ON patient_contacts(patient_id);
CREATE INDEX idx_patient_conditions_patient ON patient_conditions(patient_id, clinical_status);
CREATE INDEX idx_episodes_patient_status ON episodes_of_care(patient_id, status);
