package com.chu.sih.service;

import com.chu.sih.dto.ClinicalRequests.AppointmentCreate;
import com.chu.sih.dto.ClinicalRequests.AppointmentStaffCreate;
import com.chu.sih.dto.ClinicalRequests.AppointmentTransition;
import com.chu.sih.entity.Appointment;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.sql.Timestamp;
import java.util.*;

@Service @RequiredArgsConstructor
public class PlanningService {
    private static final Map<String,Set<String>> TRANSITIONS=Map.of(
            "PROPOSED",Set.of("BOOKED","CANCELLED"),"BOOKED",Set.of("ARRIVED","CANCELLED","NO_SHOW"),
            "ARRIVED",Set.of("FULFILLED","CANCELLED"),"FULFILLED",Set.of(),"CANCELLED",Set.of(),"NO_SHOW",Set.of());
    private final AppointmentRepository repository;
    private final ApheresisPrescriptionRepository prescriptions;
    private final EquipmentRepository equipment;
    private final UserRepository users;
    private final CurrentActor actor;
    private final AuditService audit;
    private final ClinicalAccessService access;
    private final JdbcTemplate jdbc;

    @Transactional(readOnly=true) public List<Appointment> between(Instant from,Instant to){return repository.findByStartsAtBetweenOrderByStartsAt(from,to).stream().filter(item->access.canAccessPatient(item.getPatientId())).toList();}
    @Transactional(readOnly=true) public Appointment get(UUID id){var value=repository.findById(id).orElseThrow(()->new ResourceNotFoundException("Rendez-vous introuvable."));access.requirePatient(value.getPatientId());return value;}
    @Transactional(readOnly=true) public List<Map<String,Object>> staff(UUID id){get(id);return jdbc.queryForList("select s.user_id,u.full_name,s.participation_role from appointment_staff s join users u on u.id=s.user_id where s.appointment_id=? order by u.full_name",id);}

    @Transactional
    public Appointment create(AppointmentCreate r){
        access.requirePatient(r.patientId());
        if(!r.endsAt().isAfter(r.startsAt()))throw new BadRequestException("La fin doit etre posterieure au debut.");
        if(r.startsAt().isBefore(Instant.now().minusSeconds(300)))throw new BadRequestException("Un rendez-vous ne peut pas etre cree dans le passe.");
        if(r.prescriptionId()!=null){var prescription=prescriptions.findById(r.prescriptionId()).orElseThrow(()->new ResourceNotFoundException("Prescription introuvable."));if(!prescription.getPatientId().equals(r.patientId()))throw new BadRequestException("La prescription appartient a un autre patient.");if(!List.of("VALIDATED","ACTIVE").contains(prescription.getStatus()))throw new BadRequestException("La prescription doit etre validee ou active.");}
        if(r.equipmentId()!=null){var device=equipment.findById(r.equipmentId()).orElseThrow(()->new ResourceNotFoundException("Equipement introuvable."));if(!List.of("AVAILABLE","RESERVED").contains(device.getStatus()))throw new BadRequestException("L'equipement n'est pas disponible pour la reservation.");}
        var appointment=repository.saveAndFlush(Appointment.builder().patientId(r.patientId()).prescriptionId(r.prescriptionId())
                .locationId(r.locationId()).equipmentId(r.equipmentId()).startsAt(r.startsAt()).endsAt(r.endsAt())
                .reason(r.reason()).status("BOOKED").createdBy(actor.id()).build());
        audit.record("APPOINTMENT_CREATED","CREATE","Appointment",appointment.getId(),r.patientId(),"{}");return appointment;
    }

    @Transactional
    public Map<String,Object> assignStaff(UUID appointmentId,AppointmentStaffCreate r){
        var appointment=get(appointmentId);var user=users.findByIdForUpdate(r.userId()).orElseThrow(()->new ResourceNotFoundException("Utilisateur introuvable."));
        if(!user.isEnabled())throw new BadRequestException("Le membre du personnel est desactive.");
        Integer conflicts=jdbc.queryForObject("select count(*) from appointment_staff s join appointments a on a.id=s.appointment_id where s.user_id=? and a.id<>? and a.status in ('PROPOSED','BOOKED','ARRIVED') and a.starts_at<? and a.ends_at>?",Integer.class,r.userId(),appointmentId,Timestamp.from(appointment.getEndsAt()),Timestamp.from(appointment.getStartsAt()));
        if(conflicts!=null&&conflicts>0)throw new BadRequestException("Ce membre du personnel est deja affecte sur ce creneau.");
        jdbc.update("insert into appointment_staff(appointment_id,user_id,participation_role) values (?,?,?) on conflict do nothing",appointmentId,r.userId(),r.participationRole().trim().toUpperCase());
        audit.record("APPOINTMENT_STAFF_ASSIGNED","CREATE","Appointment",appointmentId,appointment.getPatientId(),"{\"userId\":"+r.userId()+"}");
        return Map.of("appointmentId",appointmentId,"userId",r.userId(),"fullName",user.getFullName(),"participationRole",r.participationRole().trim().toUpperCase());
    }

    @Transactional
    public Appointment transition(UUID id,AppointmentTransition r){
        var value=get(id);String target=r.status().toUpperCase();
        if(!TRANSITIONS.getOrDefault(value.getStatus(),Set.of()).contains(target))throw new BadRequestException("Transition de rendez-vous interdite.");
        if(List.of("CANCELLED","NO_SHOW").contains(target)&&(r.reason()==null||r.reason().isBlank()))throw new BadRequestException("Le motif est obligatoire.");
        value.setStatus(target);audit.record("APPOINTMENT_STATUS_CHANGED","UPDATE","Appointment",id,value.getPatientId(),"{\"to\":\""+target+"\"}");return value;
    }
}
