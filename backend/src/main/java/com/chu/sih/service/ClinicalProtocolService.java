package com.chu.sih.service;

import com.chu.sih.dto.ProtocolRequests.ProtocolCreate;
import com.chu.sih.entity.ClinicalProtocol;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.ClinicalProtocolRepository;
import com.chu.sih.security.CurrentActor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class ClinicalProtocolService {
    private final ClinicalProtocolRepository repository;
    private final ObjectMapper objectMapper;
    private final CurrentActor actor;
    private final AuditService audit;

    @Transactional(readOnly=true) public List<ClinicalProtocol> active(){return repository.findByStatusOrderByNameAscVersionNumberDesc("ACTIVE");}
    @Transactional(readOnly=true) public List<ClinicalProtocol> versions(String code){return repository.findByCodeOrderByVersionNumberDesc(code.trim().toUpperCase());}
    @Transactional(readOnly=true) public ClinicalProtocol get(UUID id){return repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Protocole introuvable."));}

    @Transactional public ClinicalProtocol create(ProtocolCreate r){
        String code=r.code().trim().toUpperCase();
        int version=repository.findByCodeOrderByVersionNumberDesc(code).stream().findFirst().map(ClinicalProtocol::getVersionNumber).orElse(0)+1;
        var value=repository.save(ClinicalProtocol.builder().code(code).name(r.name().trim()).modality(r.modality().trim().toUpperCase())
                .versionNumber(version).status("DRAFT").definition(normalize(r.definition())).effectiveFrom(r.effectiveFrom()).build());
        audit.record("CLINICAL_PROTOCOL_CREATED","CREATE","ClinicalProtocol",value.getId(),null,
                "{\"code\":\""+code+"\",\"version\":"+version+"}");return value;
    }
    @Transactional public ClinicalProtocol approve(UUID id){
        var value=get(id);requireStatus(value,"DRAFT");value.setStatus("APPROVED");value.setApprovedBy(actor.id());value.setApprovedAt(Instant.now());
        audit.record("CLINICAL_PROTOCOL_APPROVED","UPDATE","ClinicalProtocol",id,null,"{}");return value;
    }
    @Transactional public ClinicalProtocol activate(UUID id){
        var value=get(id);requireStatus(value,"APPROVED");
        LocalDate start=value.getEffectiveFrom()==null?LocalDate.now():value.getEffectiveFrom();
        for(var previous:repository.findByCodeOrderByVersionNumberDesc(value.getCode())){
            if(previous.getStatus().equals("ACTIVE")){previous.setStatus("RETIRED");previous.setEffectiveUntil(start.minusDays(1));}
        }
        value.setStatus("ACTIVE");value.setEffectiveFrom(start);
        audit.record("CLINICAL_PROTOCOL_ACTIVATED","UPDATE","ClinicalProtocol",id,null,"{}");return value;
    }
    @Transactional public ClinicalProtocol retire(UUID id,String reason){
        if(reason==null||reason.isBlank())throw new BadRequestException("Le motif de retrait est obligatoire.");
        var value=get(id);requireStatus(value,"ACTIVE");value.setStatus("RETIRED");value.setEffectiveUntil(LocalDate.now());
        audit.record("CLINICAL_PROTOCOL_RETIRED","UPDATE","ClinicalProtocol",id,null,"{}");return value;
    }
    private void requireStatus(ClinicalProtocol value,String expected){if(!expected.equals(value.getStatus()))throw new BadRequestException("Le protocole doit etre au statut "+expected+".");}
    private String normalize(String json){try{return objectMapper.writeValueAsString(objectMapper.readTree(json));}catch(Exception e){throw new BadRequestException("La definition du protocole doit etre un JSON valide.");}}
}
