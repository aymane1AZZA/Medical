package com.chu.sih.controller;

import com.chu.sih.entity.AuditEvent;
import com.chu.sih.repository.AuditEventRepository;
import com.chu.sih.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController @RequestMapping("/api/admin/audit") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class AuditController {
    private final AuditEventRepository repository;
    private final AuditService auditService;
    @GetMapping public Page<AuditEvent> list(@RequestParam(required=false) UUID patientId,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="50") int size){
        var pageable=PageRequest.of(page,Math.min(Math.max(size,1),200));
        return patientId==null?repository.findAll(pageable):repository.findByPatientId(patientId,pageable);
    }
    @GetMapping("/verify") public java.util.Map<String,Object> verify(){return auditService.verifyIntegrity();}
}
