package com.chu.sih.service;

import com.chu.sih.entity.Patient;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.PatientRepository;
import com.chu.sih.repository.UserRoleAssignmentRepository;
import com.chu.sih.security.CurrentActor;
import com.chu.sih.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicalAccessService {
    private final CurrentActor currentActor;
    private final UserRoleAssignmentRepository assignments;
    private final PatientRepository patients;

    public boolean isAdmin() { return hasRole(currentActor.require(), "ROLE_ADMIN"); }
    public boolean isPatient() { return hasRole(currentActor.require(), "ROLE_PATIENT"); }
    public long currentUserId() { return currentActor.id(); }

    @Transactional(readOnly = true)
    public Set<UUID> accessibleOrganizationIds() {
        return assignments.findEffectiveByUserId(currentActor.id(), Instant.now()).stream()
                .map(assignment -> assignment.getOrganizationId())
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Transactional(readOnly = true)
    public Patient requirePatient(UUID patientId) {
        Patient patient = patients.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient introuvable."));
        if (!canAccess(patient)) throw new AccessDeniedException("Acces au dossier patient non autorise.");
        return patient;
    }

    @Transactional(readOnly = true)
    public boolean canAccessPatient(UUID patientId) {
        return patients.findById(patientId).map(this::canAccess).orElse(false);
    }

    private boolean canAccess(Patient patient) {
        UserPrincipal actor = currentActor.require();
        return hasRole(actor, "ROLE_ADMIN")
                || (hasRole(actor, "ROLE_PATIENT") && actor.getId().equals(patient.getPortalUserId()))
                || (!hasRole(actor, "ROLE_PATIENT") && accessibleOrganizationIds().contains(patient.getManagingOrganizationId()));
    }

    @Transactional(readOnly = true)
    public UUID defaultOrganizationId() {
        List<UUID> organizations = accessibleOrganizationIds().stream().sorted().toList();
        if (organizations.isEmpty()) throw new AccessDeniedException("Aucune organisation active n'est affectee a ce compte.");
        return organizations.get(0);
    }

    private boolean hasRole(UserPrincipal principal, String role) {
        return principal.getAuthorities().stream().anyMatch(authority -> role.equals(authority.getAuthority()));
    }
}
