package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.ClinicalMessageCreate;
import com.chu.sih.dto.ClinicalRequests.ClinicalThreadCreate;
import com.chu.sih.entity.ClinicalMessage;
import com.chu.sih.entity.ClinicalThread;
import com.chu.sih.service.ClinicalCommunicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clinical/communications") @RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ClinicalCommunicationController {
    private final ClinicalCommunicationService service;
    @GetMapping public List<ClinicalThread> list(){return service.list();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ClinicalThread create(@Valid @RequestBody ClinicalThreadCreate request){return service.create(request);}
    @GetMapping("/{id}") public ClinicalThread get(@PathVariable UUID id){return service.get(id);}
    @GetMapping("/{id}/messages") public List<ClinicalMessage> messages(@PathVariable UUID id){return service.messages(id);}
    @PostMapping("/{id}/messages") @ResponseStatus(HttpStatus.CREATED) public ClinicalMessage post(@PathVariable UUID id,@Valid @RequestBody ClinicalMessageCreate request){return service.post(id,request);}
    @PostMapping("/{id}/read") @ResponseStatus(HttpStatus.NO_CONTENT) public void read(@PathVariable UUID id){service.markRead(id);}
    @PostMapping("/{id}/resolve") public ClinicalThread resolve(@PathVariable UUID id){return service.resolve(id);}
}
