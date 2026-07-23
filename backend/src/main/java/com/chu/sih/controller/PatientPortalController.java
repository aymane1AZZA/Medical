package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.PatientReportedOutcomeCreate;
import com.chu.sih.entity.Patient;
import com.chu.sih.entity.PatientReportedOutcome;
import com.chu.sih.service.PatientPortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patient/portal")
@RequiredArgsConstructor
public class PatientPortalController {
    private final PatientPortalService portal;

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public Patient me() { return portal.currentPatient(); }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('PATIENT')")
    public Map<String, Object> summary() { return portal.summary(); }

    @PostMapping("/outcomes")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PATIENT')")
    public PatientReportedOutcome outcome(@Valid @RequestBody PatientReportedOutcomeCreate request) { return portal.submitOutcome(request); }
}
