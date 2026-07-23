package com.chu.sih.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record OperationalDashboardResponse(
        String role,
        Instant generatedAt,
        Map<String, Long> metrics,
        List<TrendPoint> activityTrend,
        List<WorkItem> priorityWork) {
    public record TrendPoint(LocalDate date,long sessions,long incidents) {}
    public record WorkItem(String type,String id,String title,String severity,Instant dueAt,String patientId) {}
}
