package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.PatientCreate;
import com.chu.sih.entity.Patient;
import com.chu.sih.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController @RequestMapping("/api/clinical/patients") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER','LABO','PATIENT')")
public class PatientController {
    private final PatientService service;
    @GetMapping public Page<Patient> search(@RequestParam(defaultValue="") String q,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="25") int size){return service.search(q,page,size);}
    @GetMapping("/{id}") public Patient get(@PathVariable UUID id){return service.get(id);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE')")
    public Patient create(@Valid @RequestBody PatientCreate request){return service.create(request);}
    @PostMapping("/{id}/deactivate") @PreAuthorize("hasRole('ADMIN')") public Patient deactivate(@PathVariable UUID id,@RequestParam String reason){return service.deactivate(id,reason);}
}
