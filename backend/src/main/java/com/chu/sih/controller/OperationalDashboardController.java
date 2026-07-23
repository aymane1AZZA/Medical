package com.chu.sih.controller;

import com.chu.sih.dto.OperationalDashboardResponse;
import com.chu.sih.service.OperationalDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/dashboard") @RequiredArgsConstructor @PreAuthorize("isAuthenticated()")
public class OperationalDashboardController {
    private final OperationalDashboardService service;
    @GetMapping("/operational") public OperationalDashboardResponse operational(){return service.dashboard();}
}
