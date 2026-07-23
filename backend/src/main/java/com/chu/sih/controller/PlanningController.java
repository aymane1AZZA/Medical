package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalRequests.AppointmentCreate;
import com.chu.sih.entity.Appointment;
import com.chu.sih.service.PlanningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController @RequestMapping("/api/clinical/appointments") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','MEDECIN','MEDECIN_SPECIALISTE','INFERMIER')")
public class PlanningController {
    private final PlanningService service;
    @GetMapping public List<Appointment> list(@RequestParam(required=false) Instant from,@RequestParam(required=false) Instant to){
        Instant start=from==null?Instant.now().minus(1,ChronoUnit.DAYS):from;
        Instant end=to==null?start.plus(30,ChronoUnit.DAYS):to;
        return service.between(start,end);
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public Appointment create(@Valid @RequestBody AppointmentCreate r){return service.create(r);}
    @GetMapping("/{id}/staff") public java.util.List<java.util.Map<String,Object>> staff(@PathVariable java.util.UUID id){return service.staff(id);}
    @PostMapping("/{id}/staff") @ResponseStatus(HttpStatus.CREATED) public java.util.Map<String,Object> staff(@PathVariable java.util.UUID id,@Valid @RequestBody com.chu.sih.dto.ClinicalRequests.AppointmentStaffCreate r){return service.assignStaff(id,r);}
    @PostMapping("/{id}/transition") public Appointment transition(@PathVariable java.util.UUID id,@Valid @RequestBody com.chu.sih.dto.ClinicalRequests.AppointmentTransition r){return service.transition(id,r);}
}
