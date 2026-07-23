package com.chu.sih.controller;

import com.chu.sih.dto.ProtocolRequests.ProtocolCreate;
import com.chu.sih.entity.ClinicalProtocol;
import com.chu.sih.service.ClinicalProtocolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clinical/protocols") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_GENERALISTE','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER')")
public class ClinicalProtocolController {
    private final ClinicalProtocolService service;
    @GetMapping public List<ClinicalProtocol> active(){return service.active();}
    @GetMapping("/{id}") public ClinicalProtocol get(@PathVariable UUID id){return service.get(id);}
    @GetMapping("/code/{code}/versions") public List<ClinicalProtocol> versions(@PathVariable String code){return service.versions(code);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')")
    public ClinicalProtocol create(@Valid @RequestBody ProtocolCreate r){return service.create(r);}
    @PostMapping("/{id}/approve") @PreAuthorize("hasAnyRole('MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')") public ClinicalProtocol approve(@PathVariable UUID id){return service.approve(id);}
    @PostMapping("/{id}/activate") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')") public ClinicalProtocol activate(@PathVariable UUID id){return service.activate(id);}
    @PostMapping("/{id}/retire") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE')") public ClinicalProtocol retire(@PathVariable UUID id,@RequestParam String reason){return service.retire(id,reason);}
}
