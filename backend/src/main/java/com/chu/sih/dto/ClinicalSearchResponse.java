package com.chu.sih.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ClinicalSearchResponse(
        String query,
        int total,
        Map<String, Long> countsByType,
        List<SearchHit> hits) {
    public record SearchHit(
            String type,
            String id,
            String patientId,
            String title,
            String subtitle,
            String status,
            Instant occurredAt,
            double relevance,
            String route) {}
}
