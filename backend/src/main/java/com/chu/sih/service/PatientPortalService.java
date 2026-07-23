package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.PatientReportedOutcomeCreate;
import com.chu.sih.entity.Patient;
import com.chu.sih.entity.PatientReportedOutcome;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatientPortalService {
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    private final ApheresisPrescriptionRepository prescriptions;
    private final ApheresisSessionRepository sessions;
    private final LaboratoryOrderRepository labOrders;
    private final ClinicalDocumentRepository documents;
    private final NotificationRepository notifications;
    private final PatientReportedOutcomeRepository outcomes;
    private final CurrentActor actor;
    private final ObjectMapper objectMapper;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public Map<String, Object> summary() {
        var patient = currentPatient();
        return Map.of(
                "patient", patient,
                "upcomingAppointments", appointments.findByPatientIdAndStartsAtAfterOrderByStartsAt(patient.getId(), Instant.now()).stream().limit(10).toList(),
                "prescriptions", prescriptions.findByPatientIdOrderByPrescribedAtDesc(patient.getId()).stream().limit(10).toList(),
                "sessions", sessions.findByPatientIdOrderByCreatedAtDesc(patient.getId()).stream().limit(10).toList(),
                "laboratoryOrders", labOrders.findByPatientIdOrderByOrderedAtDesc(patient.getId()).stream().limit(10).toList(),
                "documents", documents.findByPatientIdAndDeletedAtIsNullOrderByCreatedAtDesc(patient.getId()).stream()
                        .filter(document -> "PATIENT_VISIBLE".equals(document.getConfidentiality()))
                        .limit(10).toList(),
                "notifications", notifications.findByRecipientIdOrderByCreatedAtDesc(actor.id()).stream().limit(10).toList(),
                "outcomes", outcomes.findByPatientIdOrderBySubmittedAtDesc(patient.getId()).stream().limit(10).toList()
        );
    }

    @Transactional
    public PatientReportedOutcome submitOutcome(PatientReportedOutcomeCreate request) {
        var patient = currentPatient();
        if (request.sessionId() != null) {
            var session = sessions.findById(request.sessionId()).orElseThrow(() -> new ResourceNotFoundException("Seance introuvable."));
            if (!session.getPatientId().equals(patient.getId())) throw new BadRequestException("Cette seance ne vous appartient pas.");
        }
        try {
            var outcome = outcomes.save(PatientReportedOutcome.builder()
                    .patientId(patient.getId())
                    .sessionId(request.sessionId())
                    .questionnaireCode(request.questionnaireCode())
                    .response(objectMapper.writeValueAsString(request.response()))
                    .score(request.score())
                    .status("SUBMITTED")
                    .build());
            audit.record("PATIENT_OUTCOME_SUBMITTED", "CREATE", "PatientReportedOutcome", outcome.getId(), patient.getId(), "{}");
            return outcome;
        } catch (Exception exception) {
            throw new BadRequestException("Reponse patient invalide.");
        }
    }

    @Transactional(readOnly = true)
    public Patient currentPatient() {
        return patients.findByPortalUserId(actor.id()).orElseThrow(() -> new ResourceNotFoundException("Aucun dossier patient n'est lie a ce compte."));
    }
}
