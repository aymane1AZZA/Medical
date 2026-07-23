package com.chu.sih.controller;

import com.chu.sih.dto.ClinicalSearchResponse;
import com.chu.sih.service.ClinicalSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Set;

@RestController @RequestMapping("/api/search") @RequiredArgsConstructor @PreAuthorize("isAuthenticated()")
public class ClinicalSearchController {
    private final ClinicalSearchService service;
    @GetMapping("/clinical")
    public ClinicalSearchResponse search(@RequestParam String q,@RequestParam(required=false) Set<String> types,
                                         @RequestParam(required=false) Set<String> statuses,
                                         @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant from,
                                         @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME) Instant to,
                                         @RequestParam(defaultValue="30") int limit){
        return service.search(q,types,statuses,from,to,limit);
    }
}
