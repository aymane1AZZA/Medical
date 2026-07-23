package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.PrescriptionCreate;
import com.chu.sih.entity.ApheresisPrescription;
import com.chu.sih.entity.PrescriptionRequirement;
import com.chu.sih.dto.ClinicalRequests.PrescriptionRequirementCreate;
import com.chu.sih.service.PrescriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clinical/prescriptions") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER')")
public class PrescriptionController {
    private final PrescriptionService service;
    @GetMapping("/{id}") public ApheresisPrescription get(@PathVariable UUID id){return service.get(id);}
    @GetMapping public List<ApheresisPrescription> forPatient(@RequestParam UUID patientId){return service.forPatient(patientId);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public ApheresisPrescription create(@Valid @RequestBody PrescriptionCreate r){return service.create(r);}
    @GetMapping("/{id}/requirements") public List<PrescriptionRequirement> requirements(@PathVariable UUID id){return service.requirements(id);}
    @PostMapping("/{id}/requirements") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public PrescriptionRequirement requirement(@PathVariable UUID id,@Valid @RequestBody PrescriptionRequirementCreate r){return service.addRequirement(id,r);}
    @PostMapping("/{id}/submit") @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public ApheresisPrescription submit(@PathVariable UUID id){return service.transition(id,"SUBMITTED",null);}
    @PostMapping("/{id}/validate") @PreAuthorize("hasAnyRole('MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public ApheresisPrescription validate(@PathVariable UUID id){return service.transition(id,"VALIDATED",null);}
    @PostMapping("/{id}/activate") @PreAuthorize("hasAnyRole('MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public ApheresisPrescription activate(@PathVariable UUID id){return service.transition(id,"ACTIVE",null);}
    @PostMapping("/{id}/cancel") @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public ApheresisPrescription cancel(@PathVariable UUID id,@RequestParam String reason){return service.transition(id,"CANCELLED",reason);}
}
