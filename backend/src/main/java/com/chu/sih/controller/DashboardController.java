package com.chu.sih.controller;

import com.chu.sih.dto.DashboardResponse;
import com.chu.sih.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/api/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardResponse> admin() {
        return ResponseEntity.ok(dashboardService.admin());
    }

    @GetMapping("/api/medecin/dashboard")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<DashboardResponse> medecin() {
        return ResponseEntity.ok(dashboardService.medecin());
    }

    @GetMapping("/api/infirmier/dashboard")
    @PreAuthorize("hasRole('INFERMIER')")
    public ResponseEntity<DashboardResponse> infirmier() {
        return ResponseEntity.ok(dashboardService.infirmier());
    }

    @GetMapping("/api/biomedical/dashboard")
    @PreAuthorize("hasRole('BIOMEDICAL')")
    public ResponseEntity<DashboardResponse> biomedical() {
        return ResponseEntity.ok(dashboardService.biomedical());
    }

    @GetMapping("/api/patient/dashboard")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<DashboardResponse> patient() {
        return ResponseEntity.ok(dashboardService.patient());
    }

    @GetMapping("/api/labo/dashboard")
    @PreAuthorize("hasRole('LABO')")
    public ResponseEntity<DashboardResponse> labo() {
        return ResponseEntity.ok(dashboardService.labo());
    }
}
