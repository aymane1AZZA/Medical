package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.*;
import com.chu.sih.entity.ApheresisSession;
import com.chu.sih.entity.SessionChecklistItem;
import com.chu.sih.entity.SessionObservation;
import com.chu.sih.entity.SessionAlarm;
import com.chu.sih.entity.SessionConsumable;
import com.chu.sih.entity.BiologicProduct;
import com.chu.sih.service.ApheresisSessionService;
import com.chu.sih.service.SessionAlarmService;
import com.chu.sih.service.SessionSupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/clinical/sessions") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','MEDECIN_BIOLOGISTE','INFERMIER')")
public class ApheresisSessionController {
    private final ApheresisSessionService service;
    private final SessionAlarmService alarms;
    private final SessionSupplyService supplies;
    @GetMapping("/{id}") public ApheresisSession get(@PathVariable UUID id){return service.get(id);}
    @GetMapping public List<ApheresisSession> forPatient(@RequestParam UUID patientId){return service.forPatient(patientId);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ApheresisSession create(@Valid @RequestBody SessionCreate r){return service.create(r);}
    @GetMapping("/{id}/checklist") public List<SessionChecklistItem> checklist(@PathVariable UUID id){return service.checklist(id);}
    @PostMapping("/{sessionId}/checklist/{itemId}/complete") public SessionChecklistItem complete(@PathVariable UUID sessionId,@PathVariable UUID itemId,@RequestParam(required=false) String comment){return service.completeChecklist(sessionId,itemId,comment);}
    @PostMapping("/{id}/transition") public ApheresisSession transition(@PathVariable UUID id,@Valid @RequestBody Transition r){return service.transition(id,r);}
    @GetMapping("/{id}/observations") public List<SessionObservation> observations(@PathVariable UUID id){return service.observations(id);}
    @PostMapping("/{id}/observations") @ResponseStatus(HttpStatus.CREATED) public SessionObservation observe(@PathVariable UUID id,@Valid @RequestBody ObservationCreate r){return service.addObservation(id,r);}
    @PutMapping("/{id}/metrics") public ApheresisSession metrics(@PathVariable UUID id,@Valid @RequestBody SessionMetricsUpdate r){return service.updateMetrics(id,r);}
    @GetMapping("/{id}/alarms") public List<SessionAlarm> alarms(@PathVariable UUID id){return alarms.list(id);}
    @PostMapping("/{id}/alarms") @ResponseStatus(HttpStatus.CREATED) public SessionAlarm alarm(@PathVariable UUID id,@Valid @RequestBody SessionAlarmCreate r){return alarms.raise(id,r);}
    @PostMapping("/{sessionId}/alarms/{alarmId}/acknowledge") public SessionAlarm acknowledge(@PathVariable UUID sessionId,@PathVariable UUID alarmId){return alarms.acknowledge(sessionId,alarmId);}
    @PostMapping("/{sessionId}/alarms/{alarmId}/resolve") public SessionAlarm resolve(@PathVariable UUID sessionId,@PathVariable UUID alarmId,@Valid @RequestBody AlarmResolution r){return alarms.resolve(sessionId,alarmId,r);}
    @PostMapping("/{sessionId}/alarms/{alarmId}/escalate") public SessionAlarm escalate(@PathVariable UUID sessionId,@PathVariable UUID alarmId,@RequestParam long recipientId){return alarms.escalate(sessionId,alarmId,recipientId);}
    @GetMapping("/{id}/consumables") public List<SessionConsumable> consumables(@PathVariable UUID id){return supplies.consumables(id);}
    @PostMapping("/{id}/consumables") @ResponseStatus(HttpStatus.CREATED) public SessionConsumable consume(@PathVariable UUID id,@Valid @RequestBody ConsumableUse r){return supplies.consume(id,r);}
    @GetMapping("/{id}/products") public List<BiologicProduct> products(@PathVariable UUID id){return supplies.products(id);}
    @PostMapping("/{id}/products") @ResponseStatus(HttpStatus.CREATED) public BiologicProduct product(@PathVariable UUID id,@Valid @RequestBody BiologicProductCreate r){return supplies.recordProduct(id,r);}
}
