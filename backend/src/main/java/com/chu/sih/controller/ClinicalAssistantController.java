package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.AiDraftRequest;
import com.chu.sih.dto.ClinicalRequests.AiReviewRequest;
import com.chu.sih.entity.AiAssistanceRequest;
import com.chu.sih.service.ClinicalAssistantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinical/assistant")
@RequiredArgsConstructor
public class ClinicalAssistantController {
    private final ClinicalAssistantService assistant;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER','LABO')")
    public List<AiAssistanceRequest> history(@RequestParam UUID patientId) { return assistant.history(patientId); }

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER','LABO')")
    public AiAssistanceRequest draft(@Valid @RequestBody AiDraftRequest request) { return assistant.draft(request); }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public AiAssistanceRequest review(@PathVariable UUID id, @Valid @RequestBody AiReviewRequest request) { return assistant.review(id, request); }
}
