package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.LabOrderCreate;
import com.chu.sih.dto.ClinicalRequests.LabResultCreate;
import com.chu.sih.dto.ClinicalRequests.SpecimenCreate;
import com.chu.sih.entity.*;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class LaboratoryService {
    private final LaboratoryOrderRepository orders;
    private final LaboratoryOrderItemRepository items;
    private final LaboratoryResultRepository results;
    private final NotificationRepository notifications;
    private final SpecimenRepository specimens;
    private final CriticalResultAcknowledgementRepository acknowledgements;
    private final CurrentActor actor;
    private final AuditService audit;
    private final ClinicalAccessService access;

    @Transactional(readOnly=true) public List<LaboratoryOrder> forPatient(UUID patientId){access.requirePatient(patientId);return orders.findByPatientIdOrderByOrderedAtDesc(patientId);}
    @Transactional(readOnly=true) public List<LaboratoryOrderItem> items(UUID orderId){return items.findByOrderId(requireOrder(orderId).getId());}
    @Transactional(readOnly=true) public List<Specimen> specimens(UUID orderId){requireOrder(orderId);return specimens.findByOrderIdOrderByAccessionNumber(orderId);}
    @Transactional(readOnly=true) public List<CriticalResultAcknowledgement> acknowledgements(UUID resultId){requireResultAccess(resultId);return acknowledgements.findByResultIdOrderByAcknowledgedAtDesc(resultId);}

    @Transactional
    public LaboratoryOrder createOrder(LabOrderCreate r){
        access.requirePatient(r.patientId());
        var order=orders.save(LaboratoryOrder.builder().patientId(r.patientId()).prescriptionId(r.prescriptionId())
                .status("ORDERED").priority(r.priority()==null?"ROUTINE":r.priority()).orderedBy(actor.id())
                .requiredAt(r.requiredAt()).clinicalContext(r.clinicalContext()).build());
        items.saveAll(r.items().stream().map(i->LaboratoryOrderItem.builder().orderId(order.getId()).loincCode(i.loincCode())
                .display(i.display()).specimenType(i.specimenType()).status("ORDERED").build()).toList());
        audit.record("LAB_ORDER_CREATED","CREATE","LaboratoryOrder",order.getId(),r.patientId(),"{}");
        return order;
    }

    @Transactional
    public Specimen createSpecimen(SpecimenCreate r){
        var order=requireOrder(r.orderId());String suffix=UUID.randomUUID().toString().substring(0,8).toUpperCase();
        var specimen=specimens.save(Specimen.builder().orderId(r.orderId())
                .accessionNumber(blank(r.accessionNumber())==null?"ACC-"+suffix:r.accessionNumber().trim())
                .barcode(blank(r.barcode())==null?"LAB-"+suffix:r.barcode().trim())
                .specimenType(r.specimenType().trim().toUpperCase()).status("EXPECTED").build());
        audit.record("SPECIMEN_EXPECTED","CREATE","Specimen",specimen.getId(),order.getPatientId(),"{}");
        return specimen;
    }

    @Transactional
    public Specimen collectSpecimen(UUID id){
        var specimen=requireSpecimen(id);var order=requireOrder(specimen.getOrderId());
        if(!"EXPECTED".equals(specimen.getStatus()))throw new BadRequestException("Seul un prelevement attendu peut etre collecte.");
        specimen.setStatus("COLLECTED");specimen.setCollectedBy(actor.id());specimen.setCollectedAt(Instant.now());order.setStatus("COLLECTED");
        audit.record("SPECIMEN_COLLECTED","UPDATE","Specimen",id,order.getPatientId(),"{}");return specimen;
    }

    @Transactional
    public Specimen receiveSpecimen(UUID id){
        var specimen=requireSpecimen(id);var order=requireOrder(specimen.getOrderId());
        if(!"COLLECTED".equals(specimen.getStatus()))throw new BadRequestException("Seul un prelevement collecte peut etre recu.");
        specimen.setStatus("RECEIVED");specimen.setReceivedBy(actor.id());specimen.setReceivedAt(Instant.now());order.setStatus("RECEIVED");
        audit.record("SPECIMEN_RECEIVED","UPDATE","Specimen",id,order.getPatientId(),"{}");return specimen;
    }

    @Transactional
    public Specimen rejectSpecimen(UUID id,String reason){
        if(reason==null||reason.isBlank())throw new BadRequestException("Le motif de rejet est obligatoire.");
        var specimen=requireSpecimen(id);var order=requireOrder(specimen.getOrderId());
        if(List.of("REJECTED","RESULTED").contains(specimen.getStatus()))throw new BadRequestException("Ce prelevement ne peut plus etre rejete.");
        specimen.setStatus("REJECTED");specimen.setRejectionReason(reason.trim());
        audit.record("SPECIMEN_REJECTED","UPDATE","Specimen",id,order.getPatientId(),"{}");return specimen;
    }

    @Transactional
    public LaboratoryResult addResult(LabResultCreate r){
        var item=items.findById(r.orderItemId()).orElseThrow(()->new ResourceNotFoundException("Analyse demandee introuvable."));
        var order=requireOrder(item.getOrderId());
        var specimen=specimens.findById(r.specimenId()).orElseThrow(()->new ResourceNotFoundException("Prelevement introuvable."));
        if(!specimen.getOrderId().equals(order.getId()))throw new BadRequestException("Le prelevement appartient a une autre demande.");
        if(!"RECEIVED".equals(specimen.getStatus()))throw new BadRequestException("Le prelevement doit etre recu avant la saisie du resultat.");
        if(!item.getLoincCode().equals(r.loincCode()))throw new BadRequestException("Le code LOINC ne correspond pas a l'analyse demandee.");
        var result=results.save(LaboratoryResult.builder().orderItemId(r.orderItemId()).specimenId(r.specimenId())
                .loincCode(r.loincCode()).valueNumeric(r.valueNumeric()).valueText(r.valueText()).unitUcum(r.unitUcum())
                .referenceLow(r.referenceLow()).referenceHigh(r.referenceHigh()).interpretation(r.interpretation())
                .critical(r.critical()).measuredAt(r.measuredAt()).status("PRELIMINARY").build());
        item.setStatus("COMPLETED");specimen.setStatus("RESULTED");order.setStatus("PARTIAL");
        if(r.critical())notifications.save(Notification.builder().recipientId(order.getOrderedBy()).patientId(order.getPatientId())
                .notificationType("CRITICAL_LAB_RESULT").severity("CRITICAL").title("Resultat biologique critique")
                .message("Un resultat critique requiert un acquittement clinique.").actionUrl("/laboratory/results/"+result.getId())
                .requiresAcknowledgement(true).build());
        audit.record("LAB_RESULT_RECORDED","CREATE","LaboratoryResult",result.getId(),order.getPatientId(),"{\"critical\":"+r.critical()+"}");
        return result;
    }

    @Transactional
    public LaboratoryResult validate(UUID resultId){
        var context=requireResultAccess(resultId);var result=context.result();var order=context.order();
        if(!"PRELIMINARY".equals(result.getStatus()))throw new BadRequestException("Seul un resultat preliminaire peut etre valide.");
        result.setStatus("FINAL");result.setValidatedAt(Instant.now());result.setValidatedBy(actor.id());
        if(items.countByOrderIdAndStatusNot(order.getId(),"COMPLETED")==0)order.setStatus("FINAL");
        audit.record("LAB_RESULT_VALIDATED","UPDATE","LaboratoryResult",resultId,order.getPatientId(),"{}");return result;
    }

    @Transactional
    public CriticalResultAcknowledgement acknowledge(UUID resultId,String actionTaken){
        var context=requireResultAccess(resultId);var result=context.result();
        if(!result.isCritical())throw new BadRequestException("Ce resultat n'est pas critique.");
        if(acknowledgements.existsByResultId(resultId))throw new BadRequestException("Ce resultat critique est deja acquitte.");
        var value=acknowledgements.save(CriticalResultAcknowledgement.builder().resultId(resultId)
                .acknowledgedBy(actor.id()).actionTaken(actionTaken.trim()).build());
        audit.record("CRITICAL_RESULT_ACKNOWLEDGED","CREATE","CriticalResultAcknowledgement",value.getId(),context.order().getPatientId(),"{}");
        return value;
    }

    private LaboratoryOrder requireOrder(UUID id){var value=orders.findById(id).orElseThrow(()->new ResourceNotFoundException("Demande de laboratoire introuvable."));access.requirePatient(value.getPatientId());return value;}
    private Specimen requireSpecimen(UUID id){var value=specimens.findById(id).orElseThrow(()->new ResourceNotFoundException("Prelevement introuvable."));requireOrder(value.getOrderId());return value;}
    private ResultContext requireResultAccess(UUID id){var result=results.findById(id).orElseThrow(()->new ResourceNotFoundException("Resultat introuvable."));var item=items.findById(result.getOrderItemId()).orElseThrow(()->new ResourceNotFoundException("Analyse demandee introuvable."));return new ResultContext(result,requireOrder(item.getOrderId()));}
    private String blank(String value){return value==null||value.isBlank()?null:value.trim();}
    private record ResultContext(LaboratoryResult result,LaboratoryOrder order){}
}
