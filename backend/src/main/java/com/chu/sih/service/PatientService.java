package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.PatientCreate;
import com.chu.sih.entity.Patient;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.PatientRepository;
import com.chu.sih.repository.UserRepository;
import com.chu.sih.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service @RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repository;
    private final AuditService audit;
    private final ClinicalAccessService access;
    private final UserRepository users;

    @Transactional(readOnly = true)
    public Page<Patient> search(String query, int page, int size) {
        var pageable = PageRequest.of(page, Math.min(Math.max(size, 1), 100), Sort.by("familyName", "givenName"));
        if (access.isPatient()) return repository.findByActiveTrueAndPortalUserId(access.currentUserId(), pageable);
        if (access.isAdmin()) {
            if (query == null || query.isBlank()) return repository.findByActiveTrue(pageable);
            String q = query.trim();
            return repository.findByActiveTrueAndFamilyNameContainingIgnoreCaseOrActiveTrueAndGivenNameContainingIgnoreCaseOrActiveTrueAndMedicalRecordNumberContainingIgnoreCase(q, q, q, pageable);
        }
        var organizations = access.accessibleOrganizationIds();
        if (organizations.isEmpty()) return Page.empty(pageable);
        if (query == null || query.isBlank()) return repository.findByActiveTrueAndManagingOrganizationIdIn(organizations,pageable);
        String q = query.trim();
        return repository.searchAccessible(organizations,q,pageable);
    }

    @Transactional(readOnly = true)
    public Patient get(UUID id) { return access.requirePatient(id); }

    @Transactional
    public Patient create(PatientCreate request) {
        repository.findByMedicalRecordNumberIgnoreCase(request.medicalRecordNumber()).ifPresent(p -> { throw new BadRequestException("Ce numéro de dossier existe déjà."); });
        Patient patient = repository.save(Patient.builder()
                .managingOrganizationId(access.defaultOrganizationId())
                .medicalRecordNumber(request.medicalRecordNumber().trim())
                .nationalIdentifier(blankToNull(request.nationalIdentifier()))
                .familyName(request.familyName().trim().toUpperCase())
                .givenName(request.givenName().trim())
                .birthDate(request.birthDate()).administrativeGender(request.administrativeGender())
                .bloodGroup(blankToNull(request.bloodGroup())).phone(blankToNull(request.phone()))
                .email(blankToNull(request.email()))
                .preferredLanguage(request.preferredLanguage() == null ? "fr-MA" : request.preferredLanguage())
                .active(true).build());
        audit.record("PATIENT_CREATED", "CREATE", "Patient", patient.getId(), patient.getId(), "{}");
        return patient;
    }

    @Transactional
    public Patient deactivate(UUID id, String reason) {
        if (reason == null || reason.isBlank()) throw new BadRequestException("Le motif de désactivation est obligatoire.");
        Patient patient = get(id);
        patient.setActive(false);
        audit.record("PATIENT_DEACTIVATED", "UPDATE", "Patient", id, id, "{\"reasonProvided\":true}");
        return patient;
    }

    @Transactional
    public Patient linkPortalUser(UUID patientId, Long userId) {
        Patient patient = repository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable."));
        var user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable."));
        if (user.getRole() != Role.ROLE_PATIENT) {
            throw new BadRequestException("Seul un compte ayant le role patient peut etre lie au portail.");
        }
        if (!user.isEnabled()) throw new BadRequestException("Le compte patient est desactive.");
        patient.setPortalUserId(userId);
        audit.record("PATIENT_PORTAL_LINKED", "UPDATE", "Patient", patientId, patientId,
                "{\"portalUserId\":" + userId + "}");
        return patient;
    }

    private String blankToNull(String value){ return value == null || value.isBlank() ? null : value.trim(); }
}
