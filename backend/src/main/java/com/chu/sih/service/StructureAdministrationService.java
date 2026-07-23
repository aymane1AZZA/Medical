package com.chu.sih.service;

import com.chu.sih.dto.StructureRequests.*;
import com.chu.sih.entity.*;
import com.chu.sih.exception.BadRequestException;
import com.chu.sih.exception.ResourceNotFoundException;
import com.chu.sih.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class StructureAdministrationService {
    private final OrganizationRepository organizations;
    private final LocationRepository locations;
    private final CareTeamRepository careTeams;
    private final UserRepository users;
    private final JdbcTemplate jdbc;
    private final AuditService audit;

    @Transactional(readOnly=true) public List<Organization> organizations(){return organizations.findAll().stream().filter(Organization::isActive).toList();}
    @Transactional public Organization createOrganization(OrganizationCreate r){
        organizations.findByCodeAndActiveTrue(r.code().trim()).ifPresent(value->{throw new BadRequestException("Ce code d'organisation existe deja.");});
        var value=organizations.save(Organization.builder().code(r.code().trim().toUpperCase()).name(r.name().trim())
                .organizationType(r.organizationType().trim().toUpperCase()).active(true).build());
        audit.record("ORGANIZATION_CREATED","CREATE","Organization",value.getId(),null,"{}");return value;
    }
    @Transactional(readOnly=true) public List<Location> locations(UUID organizationId){requireOrganization(organizationId);return locations.findByOrganizationIdAndActiveTrueOrderByName(organizationId);}
    @Transactional public Location createLocation(LocationCreate r){
        requireOrganization(r.organizationId());
        if(r.parentId()!=null){var parent=locations.findById(r.parentId()).orElseThrow(()->new ResourceNotFoundException("Site parent introuvable."));if(!parent.getOrganizationId().equals(r.organizationId()))throw new BadRequestException("Le site parent appartient a une autre organisation.");}
        var value=locations.save(Location.builder().organizationId(r.organizationId()).parentId(r.parentId()).code(r.code().trim().toUpperCase())
                .name(r.name().trim()).locationType(r.locationType().trim().toUpperCase()).active(true).build());
        audit.record("LOCATION_CREATED","CREATE","Location",value.getId(),null,"{}");return value;
    }
    @Transactional(readOnly=true) public List<CareTeam> careTeams(UUID organizationId){requireOrganization(organizationId);return careTeams.findByOrganizationIdAndActiveTrueOrderByName(organizationId);}
    @Transactional public CareTeam createCareTeam(CareTeamCreate r){
        requireOrganization(r.organizationId());
        var value=careTeams.save(CareTeam.builder().organizationId(r.organizationId()).name(r.name().trim()).active(true).build());
        audit.record("CARE_TEAM_CREATED","CREATE","CareTeam",value.getId(),null,"{}");return value;
    }
    @Transactional public Map<String,Object> addMember(UUID careTeamId,CareTeamMemberCreate r){
        requireCareTeam(careTeamId);users.findById(r.userId()).orElseThrow(()->new ResourceNotFoundException("Utilisateur introuvable."));
        Instant validFrom=Instant.now();
        jdbc.update("insert into care_team_members(care_team_id,user_id,member_role,valid_from,valid_until) values (?,?,?,?,?)",
                careTeamId,r.userId(),r.memberRole().trim().toUpperCase(),Timestamp.from(validFrom),r.validUntil()==null?null:Timestamp.from(r.validUntil()));
        audit.record("CARE_TEAM_MEMBER_ADDED","CREATE","CareTeam",careTeamId,null,"{\"userId\":"+r.userId()+"}");
        return Map.of("careTeamId",careTeamId,"userId",r.userId(),"memberRole",r.memberRole().trim().toUpperCase(),"validFrom",validFrom);
    }
    @Transactional(readOnly=true) public List<Map<String,Object>> members(UUID careTeamId){
        requireCareTeam(careTeamId);
        return jdbc.queryForList("select member.user_id, users.full_name, member.member_role, member.valid_from, member.valid_until from care_team_members member join users on users.id=member.user_id where member.care_team_id=? and (member.valid_until is null or member.valid_until>now()) order by users.full_name",careTeamId);
    }
    private Organization requireOrganization(UUID id){return organizations.findById(id).filter(Organization::isActive).orElseThrow(()->new ResourceNotFoundException("Organisation introuvable."));}
    private CareTeam requireCareTeam(UUID id){return careTeams.findById(id).filter(CareTeam::isActive).orElseThrow(()->new ResourceNotFoundException("Equipe de soins introuvable."));}
}
