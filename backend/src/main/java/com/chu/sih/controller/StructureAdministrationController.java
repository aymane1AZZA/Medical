package com.chu.sih.controller;

import com.chu.sih.dto.StructureRequests.*;
import com.chu.sih.entity.*;
import com.chu.sih.service.StructureAdministrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController @RequestMapping("/api/admin/structure") @RequiredArgsConstructor @PreAuthorize("hasRole('ADMIN')")
public class StructureAdministrationController {
    private final StructureAdministrationService service;
    @GetMapping("/organizations") public List<Organization> organizations(){return service.organizations();}
    @PostMapping("/organizations") @ResponseStatus(HttpStatus.CREATED) public Organization organization(@Valid @RequestBody OrganizationCreate r){return service.createOrganization(r);}
    @GetMapping("/organizations/{id}/locations") public List<Location> locations(@PathVariable UUID id){return service.locations(id);}
    @PostMapping("/locations") @ResponseStatus(HttpStatus.CREATED) public Location location(@Valid @RequestBody LocationCreate r){return service.createLocation(r);}
    @GetMapping("/organizations/{id}/care-teams") public List<CareTeam> teams(@PathVariable UUID id){return service.careTeams(id);}
    @PostMapping("/care-teams") @ResponseStatus(HttpStatus.CREATED) public CareTeam team(@Valid @RequestBody CareTeamCreate r){return service.createCareTeam(r);}
    @GetMapping("/care-teams/{id}/members") public List<Map<String,Object>> members(@PathVariable UUID id){return service.members(id);}
    @PostMapping("/care-teams/{id}/members") @ResponseStatus(HttpStatus.CREATED) public Map<String,Object> member(@PathVariable UUID id,@Valid @RequestBody CareTeamMemberCreate r){return service.addMember(id,r);}
}
