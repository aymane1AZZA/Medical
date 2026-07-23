CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_patients_family_name_trgm ON patients USING gin (lower(family_name) gin_trgm_ops);
CREATE INDEX idx_patients_given_name_trgm ON patients USING gin (lower(given_name) gin_trgm_ops);
CREATE INDEX idx_patients_mrn_trgm ON patients USING gin (lower(medical_record_number) gin_trgm_ops);
CREATE INDEX idx_prescriptions_indication_trgm ON apheresis_prescriptions USING gin (lower(indication_display) gin_trgm_ops);
CREATE INDEX idx_equipment_search_trgm ON equipment USING gin (lower(manufacturer || ' ' || model || ' ' || asset_number) gin_trgm_ops);
CREATE INDEX idx_incidents_description_trgm ON incidents USING gin (lower(description) gin_trgm_ops);
CREATE INDEX idx_lab_results_loinc ON laboratory_results(loinc_code, measured_at DESC);
