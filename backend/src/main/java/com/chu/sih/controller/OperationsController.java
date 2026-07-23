package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.*;
import com.chu.sih.entity.*;
import com.chu.sih.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clinical") @RequiredArgsConstructor
public class OperationsController {
    private final SafetyService safety;
    private final EquipmentService equipment;
    private final CareCoordinationService coordination;

    @GetMapping("/incidents") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','INFERMIER','BIOMEDICAL')") public List<Incident> incidents(){return safety.open();}
    @GetMapping("/incidents/{id}") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','INFERMIER','BIOMEDICAL')") public Incident incident(@PathVariable UUID id){return safety.get(id);}
    @PostMapping("/incidents") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_SPECIALISTE','INFERMIER','BIOMEDICAL')") public Incident incident(@Valid @RequestBody IncidentCreate r){return safety.create(r);}
    @PostMapping("/incidents/{id}/assign") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN_SPECIALISTE','BIOMEDICAL')") public Incident assign(@PathVariable UUID id,@RequestParam long assigneeId){return safety.assign(id,assigneeId);}
    @PostMapping("/incidents/{id}/transition") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN_SPECIALISTE','BIOMEDICAL')") public Incident transition(@PathVariable UUID id,@Valid @RequestBody IncidentTransition r){return safety.transition(id,r);}
    @GetMapping("/incidents/{id}/actions") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','INFERMIER','BIOMEDICAL')") public List<CorrectiveAction> actions(@PathVariable UUID id){return safety.actions(id);}
    @PostMapping("/incidents/{id}/actions") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','MEDECIN_SPECIALISTE','BIOMEDICAL')") public CorrectiveAction action(@PathVariable UUID id,@Valid @RequestBody CorrectiveActionCreate r){return safety.addAction(id,r);}
    @PostMapping("/incidents/{incidentId}/actions/{actionId}/complete") @PreAuthorize("isAuthenticated()") public CorrectiveAction completeAction(@PathVariable UUID incidentId,@PathVariable UUID actionId,@Valid @RequestBody CorrectiveActionComplete r){return safety.completeAction(incidentId,actionId,r);}

    @GetMapping("/equipment") @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL','MEDECIN','INFERMIER')") public List<Equipment> equipment(@RequestParam(required=false) String status){return equipment.list(status);}
    @PostMapping("/equipment") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')") public Equipment equipment(@Valid @RequestBody EquipmentCreate r){return equipment.create(r);}
    @PostMapping("/equipment/{id}/status") @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')") public Equipment status(@PathVariable UUID id,@RequestParam String status,@RequestParam(required=false) String reason){return equipment.status(id,status,reason);}
    @GetMapping("/equipment/{id}/maintenance") @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL','MEDECIN','INFERMIER')") public List<MaintenanceWorkOrder> maintenance(@PathVariable UUID id){return equipment.workOrders(id);}
    @PostMapping("/equipment/maintenance") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')") public MaintenanceWorkOrder maintenance(@Valid @RequestBody MaintenanceWorkOrderCreate r){return equipment.createWorkOrder(r);}
    @PostMapping("/equipment/maintenance/{id}/complete") @PreAuthorize("hasAnyRole('ADMIN','BIOMEDICAL')") public MaintenanceWorkOrder completeMaintenance(@PathVariable UUID id,@Valid @RequestBody MaintenanceWorkOrderComplete r){return equipment.completeWorkOrder(id,r);}

    @GetMapping("/consents") @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','INFERMIER')") public List<Consent> consents(@RequestParam UUID patientId){return coordination.consents(patientId);}
    @PostMapping("/consents") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_SPECIALISTE','INFERMIER')") public Consent consent(@Valid @RequestBody ConsentCreate r){return coordination.grant(r);}
    @PostMapping("/consents/{id}/withdraw") @PreAuthorize("hasAnyRole('MEDECIN','MEDECIN_SPECIALISTE')") public Consent withdraw(@PathVariable UUID id,@RequestParam String reason){return coordination.withdraw(id,reason);}

    @PostMapping("/tasks") @ResponseStatus(HttpStatus.CREATED) @PreAuthorize("isAuthenticated()") public ClinicalTask task(@Valid @RequestBody TaskCreate r){return coordination.createTask(r);}
    @GetMapping("/notifications") @PreAuthorize("isAuthenticated()") public List<Notification> notifications(){return coordination.notifications();}
    @PostMapping("/notifications/{id}/read") @PreAuthorize("isAuthenticated()") public Notification read(@PathVariable UUID id,@RequestParam(defaultValue="false") boolean acknowledge){return coordination.readNotification(id,acknowledge);}
}
