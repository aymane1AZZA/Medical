package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.InventoryAdjustment;
import com.chu.sih.dto.ClinicalRequests.InventoryItemCreate;
import com.chu.sih.dto.ClinicalRequests.InventoryLotCreate;
import com.chu.sih.entity.InventoryItem;
import com.chu.sih.entity.InventoryLot;
import com.chu.sih.entity.InventoryMovement;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.InventoryItemRepository;
import com.chu.sih.repository.InventoryLotRepository;
import com.chu.sih.repository.InventoryMovementRepository;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryItemRepository items;
    private final InventoryLotRepository lots;
    private final InventoryMovementRepository movements;
    private final CurrentActor actor;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public List<InventoryItem> items() {
        return items.findByActiveTrueOrderByName();
    }

    @Transactional
    public InventoryItem createItem(InventoryItemCreate request) {
        var item = items.save(InventoryItem.builder()
                .sku(request.sku())
                .name(request.name())
                .itemType(request.itemType())
                .unit(request.unit())
                .minimumStock(request.minimumStock() == null ? BigDecimal.ZERO : request.minimumStock())
                .active(true)
                .build());
        audit.record("INVENTORY_ITEM_CREATED", "CREATE", "InventoryItem", item.getId(), null, "{}");
        return item;
    }

    @Transactional(readOnly = true)
    public List<InventoryLot> lots(UUID itemId) {
        item(itemId);
        return lots.findByItemIdOrderByExpiresOnAsc(itemId);
    }

    @Transactional
    public InventoryLot createLot(InventoryLotCreate request) {
        item(request.itemId());
        var lot = lots.save(InventoryLot.builder()
                .itemId(request.itemId())
                .lotNumber(request.lotNumber())
                .expiresOn(request.expiresOn())
                .quantityAvailable(request.quantityAvailable())
                .locationId(request.locationId())
                .quarantined(false)
                .build());
        recordMovement(lot.getId(), "RECEIPT", request.quantityAvailable(), BigDecimal.ZERO, request.quantityAvailable(), "InventoryLot", lot.getId(), "Reception initiale");
        audit.record("INVENTORY_LOT_RECEIVED", "CREATE", "InventoryLot", lot.getId(), null, "{\"itemId\":\"" + request.itemId() + "\"}");
        return lot;
    }

    @Transactional
    public InventoryLot adjust(UUID lotId, InventoryAdjustment request) {
        var lot = lockedLot(lotId);
        var before = lot.getQuantityAvailable();
        switch (request.movementType()) {
            case "ADJUSTMENT" -> lot.setQuantityAvailable(before.add(request.quantity()));
            case "DISPOSAL" -> {
                ensureQuantity(before, request.quantity());
                lot.setQuantityAvailable(before.subtract(request.quantity()));
            }
            case "QUARANTINE" -> lot.setQuarantined(true);
            case "RELEASE" -> lot.setQuarantined(false);
            default -> throw new BadRequestException("Mouvement de stock invalide.");
        }
        recordMovement(lot.getId(), request.movementType(), request.quantity(), before, lot.getQuantityAvailable(), "InventoryLot", lot.getId(), request.reason());
        audit.record("INVENTORY_LOT_ADJUSTED", "UPDATE", "InventoryLot", lot.getId(), null, "{\"movement\":\"" + request.movementType() + "\"}");
        return lot;
    }

    @Transactional(readOnly = true)
    public List<InventoryMovement> movements(UUID lotId) {
        lot(lotId);
        return movements.findByLotIdOrderByRecordedAtDesc(lotId);
    }

    @Transactional
    public void consume(UUID lotId, BigDecimal quantity, UUID sessionId) {
        var lot = lockedLot(lotId);
        if (lot.isQuarantined()) throw new BadRequestException("Ce lot est en quarantaine.");
        if (lot.getExpiresOn() != null && lot.getExpiresOn().isBefore(LocalDate.now())) throw new BadRequestException("Ce lot est perime.");
        ensureQuantity(lot.getQuantityAvailable(), quantity);
        var before = lot.getQuantityAvailable();
        lot.setQuantityAvailable(before.subtract(quantity));
        recordMovement(lotId, "CONSUMPTION", quantity, before, lot.getQuantityAvailable(), "ApheresisSession", sessionId, "Consommation en seance d'apherese");
    }

    private InventoryItem item(UUID id) {
        return items.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article de stock introuvable."));
    }

    private InventoryLot lot(UUID id) {
        return lots.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lot de stock introuvable."));
    }

    private InventoryLot lockedLot(UUID id) {
        return lots.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Lot de stock introuvable."));
    }

    private void ensureQuantity(BigDecimal available, BigDecimal requested) {
        if (available.compareTo(requested) < 0) throw new BadRequestException("Stock insuffisant pour ce lot.");
    }

    private void recordMovement(UUID lotId, String type, BigDecimal quantity, BigDecimal before, BigDecimal after, String referenceType, UUID referenceId, String reason) {
        movements.save(InventoryMovement.builder()
                .lotId(lotId)
                .movementType(type)
                .quantity(quantity)
                .quantityBefore(before)
                .quantityAfter(after)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .reason(reason)
                .recordedBy(actor.id())
                .build());
    }
}
