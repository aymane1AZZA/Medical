package com.chu.sih.repository;

import com.chu.sih.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
import java.util.Collection;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Page<Patient> findByActiveTrueAndFamilyNameContainingIgnoreCaseOrActiveTrueAndGivenNameContainingIgnoreCaseOrActiveTrueAndMedicalRecordNumberContainingIgnoreCase(
            String familyName, String givenName, String medicalRecordNumber, Pageable pageable);
    Optional<Patient> findByMedicalRecordNumberIgnoreCase(String medicalRecordNumber);
    Optional<Patient> findByPortalUserId(Long portalUserId);
    Page<Patient> findByActiveTrue(Pageable pageable);
    Page<Patient> findByActiveTrueAndManagingOrganizationIdIn(Collection<UUID> organizationIds, Pageable pageable);
    Page<Patient> findByActiveTrueAndPortalUserId(Long portalUserId, Pageable pageable);
    @Query("select patient from Patient patient where patient.active = true and patient.managingOrganizationId in :organizationIds " +
            "and (lower(patient.familyName) like lower(concat('%', :query, '%')) " +
            "or lower(patient.givenName) like lower(concat('%', :query, '%')) " +
            "or lower(patient.medicalRecordNumber) like lower(concat('%', :query, '%')))")
    Page<Patient> searchAccessible(Collection<UUID> organizationIds, String query, Pageable pageable);
}
