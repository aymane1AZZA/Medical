package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.*;
import com.chu.sih.entity.*;
import com.chu.sih.service.LaboratoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clinical/laboratory") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','LABO','MEDECIN_BIOLOGISTE','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','INFERMIER')")
public class LaboratoryController {
    private final LaboratoryService service;
    @GetMapping("/orders") public List<LaboratoryOrder> orders(@RequestParam UUID patientId){return service.forPatient(patientId);}
    @GetMapping("/orders/{id}/items") public List<LaboratoryOrderItem> items(@PathVariable UUID id){return service.items(id);}
    @PostMapping("/orders") @ResponseStatus(HttpStatus.CREATED) public LaboratoryOrder create(@Valid @RequestBody LabOrderCreate r){return service.createOrder(r);}
    @GetMapping("/orders/{id}/specimens") public List<Specimen> specimens(@PathVariable UUID id){return service.specimens(id);}
    @PostMapping("/specimens") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('LABO','INFERMIER')") public Specimen specimen(@Valid @RequestBody SpecimenCreate r){return service.createSpecimen(r);}
    @PostMapping("/specimens/{id}/collect") @PreAuthorize("hasAnyRole('LABO','INFERMIER')") public Specimen collect(@PathVariable UUID id){return service.collectSpecimen(id);}
    @PostMapping("/specimens/{id}/receive") @PreAuthorize("hasAnyRole('LABO','MEDECIN_BIOLOGISTE')") public Specimen receive(@PathVariable UUID id){return service.receiveSpecimen(id);}
    @PostMapping("/specimens/{id}/reject") @PreAuthorize("hasAnyRole('LABO','MEDECIN_BIOLOGISTE')") public Specimen reject(@PathVariable UUID id,@RequestParam String reason){return service.rejectSpecimen(id,reason);}
    @PostMapping("/results") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('LABO','MEDECIN_BIOLOGISTE')") public LaboratoryResult result(@Valid @RequestBody LabResultCreate r){return service.addResult(r);}
    @PostMapping("/results/{id}/validate") @PreAuthorize("hasRole('MEDECIN_BIOLOGISTE')") public LaboratoryResult validate(@PathVariable UUID id){return service.validate(id);}
    @GetMapping("/results/{id}/acknowledgements") public List<CriticalResultAcknowledgement> acknowledgements(@PathVariable UUID id){return service.acknowledgements(id);}
    @PostMapping("/results/{id}/acknowledge") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public CriticalResultAcknowledgement acknowledge(@PathVariable UUID id,@Valid @RequestBody CriticalResultAcknowledgementCreate r){return service.acknowledge(id,r.actionTaken());}
}
