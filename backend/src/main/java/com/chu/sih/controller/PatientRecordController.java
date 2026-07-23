package com.chu.sih.controller;

import com.chu.sih.dto.PatientRecordRequests.*;
import com.chu.sih.entity.*;
import com.chu.sih.service.PatientRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clinical/patients/{patientId}/record") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER','LABO','PATIENT')")
public class PatientRecordController {
    private final PatientRecordService service;
    @GetMapping("/identifiers") public List<PatientIdentifier> identifiers(@PathVariable UUID patientId){return service.identifiers(patientId);}
    @GetMapping("/contacts") public List<PatientContact> contacts(@PathVariable UUID patientId){return service.contacts(patientId);}
    @GetMapping("/allergies") public List<PatientAllergy> allergies(@PathVariable UUID patientId){return service.allergies(patientId);}
    @GetMapping("/conditions") public List<PatientCondition> conditions(@PathVariable UUID patientId){return service.conditions(patientId);}
    @GetMapping("/episodes") public List<EpisodeOfCare> episodes(@PathVariable UUID patientId){return service.episodes(patientId);}

    @PostMapping("/identifiers") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public PatientIdentifier identifier(@PathVariable UUID patientId,@Valid @RequestBody IdentifierCreate r){return service.addIdentifier(patientId,r);}
    @PostMapping("/contacts") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','INFERMIER')")
    public PatientContact contact(@PathVariable UUID patientId,@Valid @RequestBody ContactCreate r){return service.addContact(patientId,r);}
    @PostMapping("/allergies") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','INFERMIER')")
    public PatientAllergy allergy(@PathVariable UUID patientId,@Valid @RequestBody AllergyCreate r){return service.addAllergy(patientId,r);}
    @PostMapping("/allergies/{id}/resolve") @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public PatientAllergy resolveAllergy(@PathVariable UUID patientId,@PathVariable UUID id,@RequestParam String reason){return service.resolveAllergy(patientId,id,reason);}
    @PostMapping("/conditions") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public PatientCondition condition(@PathVariable UUID patientId,@Valid @RequestBody ConditionCreate r){return service.addCondition(patientId,r);}
    @PostMapping("/conditions/{id}/resolve") @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public PatientCondition resolveCondition(@PathVariable UUID patientId,@PathVariable UUID id,@RequestParam String reason){return service.resolveCondition(patientId,id,reason);}
    @PostMapping("/episodes") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public EpisodeOfCare episode(@PathVariable UUID patientId,@Valid @RequestBody EpisodeCreate r){return service.openEpisode(patientId,r);}
    @PostMapping("/episodes/{id}/close") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public EpisodeOfCare closeEpisode(@PathVariable UUID patientId,@PathVariable UUID id,@RequestParam String reason){return service.closeEpisode(patientId,id,reason);}
}
