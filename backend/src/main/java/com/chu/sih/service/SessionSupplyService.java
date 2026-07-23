package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.BiologicProductCreate;
import com.chu.sih.dto.ClinicalRequests.ConsumableUse;
import com.chu.sih.entity.*;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class SessionSupplyService {
    private final ApheresisSessionRepository sessions;
    private final InventoryLotRepository lots;
    private final SessionConsumableRepository consumables;
    private final BiologicProductRepository products;
    private final InventoryService inventory;
    private final CurrentActor actor;
    private final ClinicalAccessService access;
    private final AuditService audit;

    @Transactional(readOnly=true) public List<SessionConsumable> consumables(UUID sessionId){session(sessionId);return consumables.findBySessionIdOrderByRecordedAtDesc(sessionId);}
    @Transactional(readOnly=true) public List<BiologicProduct> products(UUID sessionId){session(sessionId);return products.findBySessionIdOrderByRecordedAtDesc(sessionId);}

    @Transactional public SessionConsumable consume(UUID sessionId, ConsumableUse request){
        var session=session(sessionId);requireMutable(session);
        var lot=lots.findById(request.inventoryLotId()).orElseThrow(()->new ResourceNotFoundException("Lot de stock introuvable."));
        inventory.consume(lot.getId(),request.quantity(),sessionId);
        var use=consumables.save(SessionConsumable.builder().sessionId(sessionId).inventoryLotId(lot.getId())
                .quantity(request.quantity()).recordedBy(actor.id()).build());
        audit.record("SESSION_CONSUMABLE_USED","CREATE","SessionConsumable",use.getId(),session.getPatientId(),"{\"lotId\":\""+lot.getId()+"\"}");
        return use;
    }

    @Transactional public BiologicProduct recordProduct(UUID sessionId, BiologicProductCreate request){
        var session=session(sessionId);requireMutable(session);
        var product=products.save(BiologicProduct.builder().sessionId(sessionId).productIdentifier(request.productIdentifier())
                .productType(request.productType()).bloodGroup(request.bloodGroup()).volumeMl(request.volumeMl())
                .expiresAt(request.expiresAt()).disposition(request.disposition()).build());
        audit.record("BIOLOGIC_PRODUCT_RECORDED","CREATE","BiologicProduct",product.getId(),session.getPatientId(),"{}");
        return product;
    }

    private ApheresisSession session(UUID id){var value=sessions.findById(id).orElseThrow(()->new ResourceNotFoundException("Seance introuvable."));access.requirePatient(value.getPatientId());return value;}
    private void requireMutable(ApheresisSession session){if(!List.of("READY","IN_PROGRESS","PAUSED").contains(session.getStatus()))throw new BadRequestException("La seance n'accepte plus de consommation ou produit.");}
}
