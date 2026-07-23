package com.chu.sih.controller;

import com.chu.sih.entity.Patient;
import com.chu.sih.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/patient-identities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class PatientIdentityAdminController {
    private final PatientService patients;

    @PostMapping("/{patientId}/portal-users/{userId}")
    public Patient linkPortalUser(@PathVariable UUID patientId, @PathVariable Long userId) {
        return patients.linkPortalUser(patientId, userId);
    }
}
