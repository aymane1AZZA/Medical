package com.chu.sih.service;

import com.chu.sih.dto.OperationalDashboardResponse;
import com.chu.sih.dto.OperationalDashboardResponse.TrendPoint;
import com.chu.sih.dto.OperationalDashboardResponse.WorkItem;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor
public class OperationalDashboardService {
    private final NamedParameterJdbcTemplate jdbc;
    private final ClinicalAccessService access;
    private final CurrentActor actor;

    @Transactional(readOnly=true)
    public OperationalDashboardResponse dashboard(){
        String role=actor.require().getAuthorities().iterator().next().getAuthority();
        Scope scope=scope(role);
        Map<String,Long> metrics=new LinkedHashMap<>();
        metrics.put("activePatients",count("select count(*) from patients p where p.active and "+scope.patientPredicate,scope.params));
        metrics.put("appointmentsToday",count("select count(*) from appointments a join patients p on p.id=a.patient_id where a.status<>'CANCELLED' and a.starts_at>=current_date and a.starts_at<current_date+interval '1 day' and "+scope.patientPredicate,scope.params));
        metrics.put("sessionsInProgress",count("select count(*) from apheresis_sessions s join patients p on p.id=s.patient_id where s.status in ('READY','IN_PROGRESS','PAUSED') and "+scope.patientPredicate,scope.params));
        metrics.put("prescriptionsPending",count("select count(*) from apheresis_prescriptions pr join patients p on p.id=pr.patient_id where pr.status in ('SUBMITTED','VALIDATED') and "+scope.patientPredicate,scope.params));
        metrics.put("criticalResults",count("select count(*) from laboratory_results r join laboratory_order_items i on i.id=r.order_item_id join laboratory_orders o on o.id=i.order_id join patients p on p.id=o.patient_id where r.is_critical=true and not exists(select 1 from critical_result_acknowledgements a where a.result_id=r.id) and "+scope.patientPredicate,scope.params));
        metrics.put("openIncidents",count("select count(*) from incidents x join patients p on p.id=x.patient_id where x.status<>'CLOSED' and "+scope.patientPredicate,scope.params));
        metrics.put("overdueTasks",count("select count(*) from clinical_tasks t join patients p on p.id=t.patient_id where t.status not in ('COMPLETED','CANCELLED') and t.due_at<now() and "+scope.patientPredicate,scope.params));
        metrics.put("equipmentUnavailable",role.equals("ROLE_PATIENT")?0:countEquipment(scope));
        return new OperationalDashboardResponse(role,Instant.now(),metrics,trend(scope),work(scope));
    }

    private List<TrendPoint> trend(Scope scope){
        String sql="""
                with days(day_value) as (select generate_series(current_date-13,current_date,interval '1 day')::date),
                session_counts as (select s.created_at::date day_value,count(*) total from apheresis_sessions s join patients p on p.id=s.patient_id where %s group by 1),
                incident_counts as (select x.occurred_at::date day_value,count(*) total from incidents x join patients p on p.id=x.patient_id where %s group by 1)
                select d.day_value,coalesce(s.total,0) sessions,coalesce(i.total,0) incidents from days d left join session_counts s on s.day_value=d.day_value left join incident_counts i on i.day_value=d.day_value order by d.day_value
                """.formatted(scope.patientPredicate,scope.patientPredicate);
        return jdbc.query(sql,scope.params,(rs,row)->new TrendPoint(rs.getObject("day_value",LocalDate.class),rs.getLong("sessions"),rs.getLong("incidents")));
    }

    private List<WorkItem> work(Scope scope){
        String sql="""
                select 'TASK' type,t.id::text id,t.description title,t.priority severity,t.due_at,p.id::text patient_id
                from clinical_tasks t join patients p on p.id=t.patient_id
                where t.status not in ('COMPLETED','CANCELLED') and %s
                order by case t.priority when 'STAT' then 1 when 'URGENT' then 2 else 3 end,t.due_at nulls last limit 12
                """.formatted(scope.patientPredicate);
        return jdbc.query(sql,scope.params,(rs,row)->new WorkItem(rs.getString("type"),rs.getString("id"),rs.getString("title"),rs.getString("severity"),toInstant(rs.getTimestamp("due_at")),rs.getString("patient_id")));
    }

    private long count(String sql,MapSqlParameterSource params){Long value=jdbc.queryForObject(sql,params,Long.class);return value==null?0:value;}
    private long countEquipment(Scope scope){
        if(scope.admin)return count("select count(*) from equipment where status not in ('AVAILABLE','IN_USE')",scope.params);
        return count("select count(*) from equipment e join locations l on l.id=e.location_id where e.status not in ('AVAILABLE','IN_USE') and l.organization_id in (:orgs)",scope.params);
    }
    private Scope scope(String role){
        var params=new MapSqlParameterSource().addValue("userId",actor.id());
        if(role.equals("ROLE_ADMIN"))return new Scope("true",params,true);
        if(role.equals("ROLE_PATIENT"))return new Scope("p.portal_user_id=:userId",params,false);
        Set<UUID> orgs=access.accessibleOrganizationIds();
        params.addValue("orgs",orgs.isEmpty()?List.of(UUID.randomUUID()):orgs);
        return new Scope("p.managing_organization_id in (:orgs)",params,false);
    }
    private Instant toInstant(Timestamp value){return value==null?null:value.toInstant();}
    private record Scope(String patientPredicate,MapSqlParameterSource params,boolean admin){}
}
