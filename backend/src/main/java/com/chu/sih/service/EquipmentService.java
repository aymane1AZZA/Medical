package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.EquipmentCreate;
import com.chu.sih.dto.ClinicalRequests.MaintenanceWorkOrderComplete;
import com.chu.sih.dto.ClinicalRequests.MaintenanceWorkOrderCreate;
import com.chu.sih.entity.Equipment;
import com.chu.sih.entity.MaintenanceWorkOrder;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.EquipmentRepository;
import com.chu.sih.repository.MaintenanceWorkOrderRepository;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private static final Set<String> STATUSES = Set.of("AVAILABLE", "RESERVED", "IN_USE", "MAINTENANCE", "OUT_OF_SERVICE", "RETIRED");

    private final EquipmentRepository repository;
    private final MaintenanceWorkOrderRepository workOrders;
    private final CurrentActor actor;
    private final AuditService audit;

    @Transactional(readOnly = true)
    public List<Equipment> list(String status) {
        return status == null ? repository.findAll() : repository.findByStatusOrderByAssetNumber(status);
    }

    @Transactional(readOnly = true)
    public Equipment get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Equipement introuvable."));
    }

    @Transactional
    public Equipment create(EquipmentCreate request) {
        var equipment = repository.save(Equipment.builder()
                .assetNumber(request.assetNumber())
                .udi(request.udi())
                .manufacturer(request.manufacturer())
                .model(request.model())
                .serialNumber(request.serialNumber())
                .equipmentType(request.equipmentType())
                .locationId(request.locationId())
                .commissionedOn(request.commissionedOn())
                .nextMaintenanceAt(request.nextMaintenanceAt())
                .firmwareVersion(request.firmwareVersion())
                .status("AVAILABLE")
                .build());
        audit.record("EQUIPMENT_CREATED", "CREATE", "Equipment", equipment.getId(), null, "{}");
        return equipment;
    }

    @Transactional
    public Equipment status(UUID id, String status, String reason) {
        String normalized = status.toUpperCase();
        if (!STATUSES.contains(normalized)) throw new BadRequestException("Statut d'equipement invalide.");
        if ((normalized.equals("OUT_OF_SERVICE") || normalized.equals("RETIRED")) && (reason == null || reason.isBlank())) {
            throw new BadRequestException("Un motif est obligatoire.");
        }
        var equipment = get(id);
        if (equipment.getStatus().equals("IN_USE") && normalized.equals("RETIRED")) {
            throw new BadRequestException("Un appareil en cours d'utilisation ne peut pas etre retire.");
        }
        equipment.setStatus(normalized);
        audit.record("EQUIPMENT_STATUS_CHANGED", "UPDATE", "Equipment", id, null, "{\"to\":\"" + normalized + "\"}");
        return equipment;
    }

    @Transactional(readOnly = true)
    public List<MaintenanceWorkOrder> workOrders(UUID equipmentId) {
        get(equipmentId);
        return workOrders.findByEquipmentIdOrderByOpenedAtDesc(equipmentId);
    }

    @Transactional
    public MaintenanceWorkOrder createWorkOrder(MaintenanceWorkOrderCreate request) {
        var equipment = get(request.equipmentId());
        if (equipment.getStatus().equals("RETIRED")) {
            throw new BadRequestException("Un equipement retire ne peut plus recevoir d'ordre de maintenance.");
        }
        if (workOrders.existsByEquipmentIdAndStatusIn(equipment.getId(), List.of("OPEN", "PLANNED", "IN_PROGRESS"))) {
            throw new BadRequestException("Un ordre de maintenance actif existe deja pour cet equipement.");
        }
        var workOrder = workOrders.save(MaintenanceWorkOrder.builder()
                .equipmentId(equipment.getId())
                .workOrderNumber("MWO-" + Instant.now().toEpochMilli())
                .maintenanceType(request.maintenanceType())
                .status(request.scheduledAt() == null ? "OPEN" : "PLANNED")
                .priority(request.priority())
                .description(request.description())
                .openedBy(actor.id())
                .assignedTo(request.assignedTo())
                .scheduledAt(request.scheduledAt())
                .build());
        if (request.priority().equals("CRITICAL") || request.maintenanceType().equals("CORRECTIVE")) {
            equipment.setStatus("MAINTENANCE");
        }
        audit.record("MAINTENANCE_ORDER_CREATED", "CREATE", "MaintenanceWorkOrder", workOrder.getId(), null,
                "{\"equipmentId\":\"" + equipment.getId() + "\"}");
        return workOrder;
    }

    @Transactional
    public MaintenanceWorkOrder completeWorkOrder(UUID id, MaintenanceWorkOrderComplete request) {
        var workOrder = workOrders.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ordre de maintenance introuvable."));
        var equipment = get(workOrder.getEquipmentId());
        if (workOrder.getStatus().equals("COMPLETED") || workOrder.getStatus().equals("CANCELLED")) {
            throw new BadRequestException("Cet ordre de maintenance est deja clos.");
        }
        workOrder.setStatus("COMPLETED");
        workOrder.setCompletedAt(Instant.now());
        workOrder.setCompletionNotes(request.completionNotes());
        workOrder.setNextDueAt(request.nextDueAt());
        if (request.nextDueAt() != null) equipment.setNextMaintenanceAt(request.nextDueAt());
        if (equipment.getStatus().equals("MAINTENANCE")) equipment.setStatus("AVAILABLE");
        audit.record("MAINTENANCE_ORDER_COMPLETED", "UPDATE", "MaintenanceWorkOrder", workOrder.getId(), null,
                "{\"equipmentId\":\"" + equipment.getId() + "\"}");
        return workOrder;
    }
}
