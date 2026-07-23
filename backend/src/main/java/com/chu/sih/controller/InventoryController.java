package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.InventoryAdjustment;
import com.chu.sih.dto.ClinicalRequests.InventoryItemCreate;
import com.chu.sih.dto.ClinicalRequests.InventoryLotCreate;
import com.chu.sih.entity.InventoryItem;
import com.chu.sih.entity.InventoryLot;
import com.chu.sih.entity.InventoryMovement;
import com.chu.sih.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clinical/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventory;

    @GetMapping("/items")
    @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL','INFERMIER','MEDECIN','MEDECIN_SPECIALISTE')")
    public List<InventoryItem> items() { return inventory.items(); }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')")
    public InventoryItem item(@Valid @RequestBody InventoryItemCreate request) { return inventory.createItem(request); }

    @GetMapping("/items/{itemId}/lots")
    @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL','INFERMIER','MEDECIN','MEDECIN_SPECIALISTE')")
    public List<InventoryLot> lots(@PathVariable UUID itemId) { return inventory.lots(itemId); }

    @PostMapping("/lots")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')")
    public InventoryLot lot(@Valid @RequestBody InventoryLotCreate request) { return inventory.createLot(request); }

    @PostMapping("/lots/{lotId}/adjust")
    @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')")
    public InventoryLot adjust(@PathVariable UUID lotId, @Valid @RequestBody InventoryAdjustment request) { return inventory.adjust(lotId, request); }

    @GetMapping("/lots/{lotId}/movements")
    @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')")
    public List<InventoryMovement> movements(@PathVariable UUID lotId) { return inventory.movements(lotId); }
}
