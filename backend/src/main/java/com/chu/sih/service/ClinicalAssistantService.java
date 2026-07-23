package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.AiDraftRequest;
import com.chu.sih.dto.ClinicalRequests.AiReviewRequest;
import com.chu.sih.entity.AiAssistanceRequest;
import com.chu.sih.entity.ApheresisSession;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClinicalAssistantService {
    private final AiAssistanceRequestRepository requests;
    private final PatientRepository patients;
    private final ApheresisSessionRepository sessions;
    private final SessionObservationRepository observations;
    private final SessionAlarmRepository alarms;
    private final IncidentRepository incidents;
    private final LaboratoryOrderRepository labOrders;
    private final ClinicalAccessService access;
    private final CurrentActor actor;
    private final ObjectMapper objectMapper;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public List<AiAssistanceRequest> history(UUID patientId) {
        access.requirePatient(patientId);
        return requests.findByPatientIdOrderByCreatedAtDesc(patientId);
    }

    @Transactional
    public AiAssistanceRequest draft(AiDraftRequest request) {
        var patient = access.requirePatient(request.patientId());
        ApheresisSession session = null;
        if (request.sessionId() != null) {
            session = sessions.findById(request.sessionId()).orElseThrow(() -> new ResourceNotFoundException("Seance introuvable."));
            if (!session.getPatientId().equals(patient.getId())) throw new BadRequestException("La seance ne correspond pas au patient.");
        }
        var facts = facts(patient.getId(), session);
        var flags = riskFlags(session);
        var output = generate(request, patient.getGivenName() + " " + patient.getFamilyName(), session, flags);
        try {
            var draft = requests.save(AiAssistanceRequest.builder()
                    .patientId(patient.getId())
                    .sessionId(session == null ? null : session.getId())
                    .assistanceType(request.assistanceType())
                    .purpose(request.purpose())
                    .clinicianInput(request.clinicianInput())
                    .generatedOutput(output)
                    .riskFlags(objectMapper.writeValueAsString(flags))
                    .sourceFacts(objectMapper.writeValueAsString(facts))
                    .createdBy(actor.id())
                    .status("GENERATED")
                    .build());
            audit.record("AI_ASSISTANCE_GENERATED", "CREATE", "AiAssistanceRequest", draft.getId(), patient.getId(),
                    "{\"type\":\"" + request.assistanceType() + "\"}");
            return draft;
        } catch (Exception exception) {
            throw new BadRequestException("Impossible de generer l'assistance clinique.");
        }
    }

    @Transactional
    public AiAssistanceRequest review(UUID id, AiReviewRequest request) {
        var draft = requests.findById(id).orElseThrow(() -> new ResourceNotFoundException("Assistance clinique introuvable."));
        access.requirePatient(draft.getPatientId());
        if (!draft.getStatus().equals("GENERATED")) throw new BadRequestException("Cette proposition a deja ete revue.");
        draft.setStatus(request.status());
        draft.setReviewedBy(actor.id());
        draft.setReviewedAt(Instant.now());
        draft.setReviewNote(request.reviewNote());
        audit.record("AI_ASSISTANCE_REVIEWED", "UPDATE", "AiAssistanceRequest", draft.getId(), draft.getPatientId(),
                "{\"status\":\"" + request.status() + "\"}");
        return draft;
    }

    private Map<String, Object> facts(UUID patientId, ApheresisSession session) {
        Map<String, Object> facts = new LinkedHashMap<>();
        facts.put("patientId", patientId);
        facts.put("openIncidents", incidents.findByPatientIdOrderByOccurredAtDesc(patientId).stream().filter(i -> !"CLOSED".equals(i.getStatus())).limit(5).toList());
        facts.put("recentLabOrders", labOrders.findByPatientIdOrderByOrderedAtDesc(patientId).stream().limit(5).toList());
        if (session != null) {
            facts.put("session", session);
            facts.put("recentObservations", observations.findBySessionIdOrderByObservedAtDesc(session.getId()).stream().limit(12).toList());
            facts.put("alarms", alarms.findBySessionIdOrderByRaisedAtDesc(session.getId()).stream().limit(12).toList());
        }
        return facts;
    }

    private List<String> riskFlags(ApheresisSession session) {
        List<String> flags = new ArrayList<>();
        if (session == null) {
            flags.add("NO_SESSION_CONTEXT");
            return flags;
        }
        if (alarms.countBySessionIdAndSeverityAndResolvedAtIsNull(session.getId(), "CRITICAL") > 0) flags.add("UNRESOLVED_CRITICAL_ALARM");
        if (alarms.countBySessionIdAndResolvedAtIsNull(session.getId()) > 0) flags.add("UNRESOLVED_ALARMS");
        if (session.getActualProcessedVolumeMl() == null || session.getActualProcessedVolumeMl().signum() <= 0) flags.add("MISSING_PROCESSED_VOLUME");
        if (!List.of("IN_PROGRESS", "PAUSED", "COMPLETED", "VALIDATED").contains(session.getStatus())) flags.add("SESSION_NOT_STARTED");
        return flags;
    }

    private String generate(AiDraftRequest request, String patientName, ApheresisSession session, List<String> flags) {
        StringBuilder output = new StringBuilder();
        output.append("Proposition ").append(request.assistanceType()).append(" pour ").append(patientName).append(".\n");
        output.append("Objectif: ").append(request.purpose()).append(".\n");
        if (request.clinicianInput() != null && !request.clinicianInput().isBlank()) {
            output.append("Contexte saisi par le clinicien: ").append(request.clinicianInput()).append("\n");
        }
        if (session != null) {
            output.append("Seance: statut ").append(session.getStatus()).append(", sequence ").append(session.getSequenceNumber()).append(".");
            if (session.getActualProcessedVolumeMl() != null) output.append(" Volume traite ").append(session.getActualProcessedVolumeMl()).append(" ml.");
            output.append("\n");
        }
        if (request.assistanceType().equals("DATA_QUALITY")) {
            output.append(flags.isEmpty() ? "Controle qualite: aucune anomalie bloquante detectee." : "Controle qualite: points a verifier - " + String.join(", ", flags) + ".");
        } else if (request.assistanceType().equals("PATIENT_EXPLANATION")) {
            output.append("Message patient: resume simple, rassurant et sans consigne therapeutique autonome. Confirmer les consignes finales avec l'equipe soignante.");
        } else {
            output.append("Synthese: verifier les constantes, les alarmes, le bilan biologique recent et les incidents ouverts avant validation humaine.");
        }
        output.append("\nCette aide est un brouillon soumis a validation humaine; elle ne remplace pas une decision medicale.");
        return output.toString();
    }
}
