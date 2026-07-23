ALTER TABLE user_role_assignments
    ADD COLUMN access_scope VARCHAR(24) NOT NULL DEFAULT 'ORGANIZATION';
ALTER TABLE user_role_assignments
    ADD CONSTRAINT ck_user_role_access_scope
    CHECK (access_scope IN ('ORGANIZATION', 'LOCATION', 'CARE_TEAM', 'SELF'));

INSERT INTO user_role_assignments(user_id, role_code, organization_id, access_scope)
SELECT users.id,
       users.role,
       organizations.id,
       CASE WHEN users.role = 'ROLE_PATIENT' THEN 'SELF' ELSE 'ORGANIZATION' END
FROM users
CROSS JOIN organizations
WHERE organizations.code = 'CHU-M6-MRK'
  AND NOT EXISTS (
      SELECT 1 FROM user_role_assignments assignment
      WHERE assignment.user_id = users.id AND assignment.active
  );

ALTER TABLE patients ADD COLUMN managing_organization_id UUID REFERENCES organizations(id);
ALTER TABLE patients ADD COLUMN portal_user_id BIGINT REFERENCES users(id);

UPDATE patients
SET managing_organization_id = (
    SELECT id FROM organizations WHERE code = 'CHU-M6-MRK'
)
WHERE managing_organization_id IS NULL;

ALTER TABLE patients ALTER COLUMN managing_organization_id SET NOT NULL;
CREATE INDEX idx_patients_managing_organization ON patients(managing_organization_id, active);
CREATE UNIQUE INDEX uq_patients_portal_user ON patients(portal_user_id) WHERE portal_user_id IS NOT NULL;

INSERT INTO locations(organization_id, code, name, location_type)
SELECT id, 'APH-UNIT', 'Unite d''apherese', 'DEPARTMENT'
FROM organizations
WHERE code = 'CHU-M6-MRK'
ON CONFLICT (organization_id, code) DO NOTHING;
