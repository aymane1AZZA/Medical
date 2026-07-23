package com.chu.sih.service;

import com.chu.sih.dto.ClinicalSearchResponse;
import com.chu.sih.dto.ClinicalSearchResponse.SearchHit;
import com.chu.sih.security.CurrentActor;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ClinicalSearchService {
    private static final Set<String> ALLOWED_TYPES=Set.of("PATIENT","PRESCRIPTION","SESSION","LAB_RESULT","EQUIPMENT","INCIDENT");
    private final NamedParameterJdbcTemplate jdbc;
    private final ClinicalAccessService access;
    private final CurrentActor actor;

    @Transactional(readOnly=true)
    public ClinicalSearchResponse search(String rawQuery,Set<String> requestedTypes,Set<String> statuses,Instant from,Instant to,int requestedLimit){
        String query=rawQuery==null?"":rawQuery.trim();
        if(query.length()<2)throw new IllegalArgumentException("La recherche doit contenir au moins 2 caracteres.");
        int limit=Math.min(Math.max(requestedLimit,1),100);
        Set<String> types=requestedTypes==null||requestedTypes.isEmpty()?ALLOWED_TYPES:requestedTypes.stream().map(String::toUpperCase).filter(ALLOWED_TYPES::contains).collect(Collectors.toSet());
        Scope scope=scope();
        boolean allStatuses=statuses==null||statuses.isEmpty();
        scope.params.addValue("query",query).addValue("pattern","%"+query+"%").addValue("limit",limit)
                .addValue("allStatuses",allStatuses)
                .addValue("statuses",allStatuses?List.of("__ANY__"):statuses.stream().map(String::toUpperCase).toList())
                .addValue("hasFrom",from!=null).addValue("hasTo",to!=null)
                .addValue("from",Timestamp.from(from==null?Instant.EPOCH:from),Types.TIMESTAMP)
                .addValue("to",Timestamp.from(to==null?Instant.parse("2100-01-01T00:00:00Z"):to),Types.TIMESTAMP);
        List<SearchHit> hits=new ArrayList<>();
        if(types.contains("PATIENT"))hits.addAll(patients(scope));
        if(types.contains("PRESCRIPTION"))hits.addAll(prescriptions(scope));
        if(types.contains("SESSION"))hits.addAll(sessions(scope));
        if(types.contains("LAB_RESULT"))hits.addAll(results(scope));
        if(types.contains("INCIDENT"))hits.addAll(incidents(scope));
        if(types.contains("EQUIPMENT")&&!scope.patient)hits.addAll(equipment(scope));
        List<SearchHit> sorted=hits.stream().sorted(Comparator.comparingDouble(SearchHit::relevance).reversed().thenComparing(SearchHit::occurredAt,Comparator.nullsLast(Comparator.reverseOrder()))).limit(limit).toList();
        Map<String,Long> counts=sorted.stream().collect(Collectors.groupingBy(SearchHit::type,LinkedHashMap::new,Collectors.counting()));
        return new ClinicalSearchResponse(query,sorted.size(),counts,sorted);
    }

    private List<SearchHit> patients(Scope s){return query("""
            select 'PATIENT' type,p.id::text id,p.id::text patient_id,p.family_name||' '||p.given_name title,
            p.medical_record_number||' · '||to_char(p.birth_date,'DD/MM/YYYY') subtitle,case when p.active then 'ACTIVE' else 'INACTIVE' end status,
            p.updated_at occurred_at,greatest(similarity(lower(p.family_name),lower(:query)),similarity(lower(p.given_name),lower(:query)),similarity(lower(p.medical_record_number),lower(:query))) relevance,
            '/patients/'||p.id route from patients p where %s and (p.family_name ilike :pattern or p.given_name ilike :pattern or p.medical_record_number ilike :pattern or coalesce(p.national_identifier,'') ilike :pattern) order by relevance desc limit :limit
            """.formatted(s.patientPredicate),s);}
    private List<SearchHit> prescriptions(Scope s){return query("""
            select 'PRESCRIPTION' type,pr.id::text id,p.id::text patient_id,pr.indication_display title,
            p.family_name||' '||p.given_name||' · '||pr.modality subtitle,pr.status,pr.prescribed_at occurred_at,
            greatest(similarity(lower(pr.indication_display),lower(:query)),similarity(lower(pr.indication_code),lower(:query))) relevance,
            '/prescriptions/'||pr.id route from apheresis_prescriptions pr join patients p on p.id=pr.patient_id where %s and (pr.indication_display ilike :pattern or pr.indication_code ilike :pattern or pr.modality ilike :pattern) and (:allStatuses=true or pr.status in (:statuses)) and (:hasFrom=false or pr.prescribed_at>=:from) and (:hasTo=false or pr.prescribed_at<:to) order by relevance desc limit :limit
            """.formatted(s.patientPredicate),s);}
    private List<SearchHit> sessions(Scope s){return query("""
            select 'SESSION' type,se.id::text id,p.id::text patient_id,se.session_number title,
            p.family_name||' '||p.given_name||' · seance '||se.sequence_number subtitle,se.status,se.created_at occurred_at,
            greatest(similarity(lower(se.session_number),lower(:query)),similarity(lower(p.family_name||' '||p.given_name),lower(:query))) relevance,
            '/sessions/'||se.id route from apheresis_sessions se join patients p on p.id=se.patient_id where %s and (se.session_number ilike :pattern or p.family_name ilike :pattern or p.given_name ilike :pattern) and (:allStatuses=true or se.status in (:statuses)) and (:hasFrom=false or se.created_at>=:from) and (:hasTo=false or se.created_at<:to) order by relevance desc limit :limit
            """.formatted(s.patientPredicate),s);}
    private List<SearchHit> results(Scope s){return query("""
            select 'LAB_RESULT' type,r.id::text id,p.id::text patient_id,i.display title,
            p.family_name||' '||p.given_name||' · '||coalesce(r.value_text,r.value_numeric::text)||' '||coalesce(r.unit_ucum,'') subtitle,r.status,r.measured_at occurred_at,
            greatest(similarity(lower(i.display),lower(:query)),similarity(lower(r.loinc_code),lower(:query))) relevance,
            '/laboratory/results/'||r.id route from laboratory_results r join laboratory_order_items i on i.id=r.order_item_id join laboratory_orders o on o.id=i.order_id join patients p on p.id=o.patient_id where %s and (i.display ilike :pattern or r.loinc_code ilike :pattern or coalesce(r.value_text,'') ilike :pattern) and (:allStatuses=true or r.status in (:statuses)) and (:hasFrom=false or r.measured_at>=:from) and (:hasTo=false or r.measured_at<:to) order by relevance desc limit :limit
            """.formatted(s.patientPredicate),s);}
    private List<SearchHit> incidents(Scope s){return query("""
            select 'INCIDENT' type,x.id::text id,p.id::text patient_id,x.incident_number title,
            x.category||' · '||left(x.description,100) subtitle,x.status,x.occurred_at,
            greatest(similarity(lower(x.description),lower(:query)),similarity(lower(x.incident_number),lower(:query))) relevance,
            '/incidents/'||x.id route from incidents x join patients p on p.id=x.patient_id where %s and (x.description ilike :pattern or x.incident_number ilike :pattern or x.category ilike :pattern) and (:allStatuses=true or x.status in (:statuses)) and (:hasFrom=false or x.occurred_at>=:from) and (:hasTo=false or x.occurred_at<:to) order by relevance desc limit :limit
            """.formatted(s.patientPredicate),s);}
    private List<SearchHit> equipment(Scope s){return query("""
            select 'EQUIPMENT' type,e.id::text id,null patient_id,e.manufacturer||' '||e.model title,
            e.asset_number||' · '||e.serial_number subtitle,e.status,e.updated_at occurred_at,
            similarity(lower(e.manufacturer||' '||e.model||' '||e.asset_number||' '||e.serial_number),lower(:query)) relevance,
            '/equipment/'||e.id route from equipment e left join locations l on l.id=e.location_id where %s and (e.manufacturer ilike :pattern or e.model ilike :pattern or e.asset_number ilike :pattern or e.serial_number ilike :pattern) and (:allStatuses=true or e.status in (:statuses)) order by relevance desc limit :limit
            """.formatted(s.admin?"true":"l.organization_id in (:orgs)"),s);}
    private List<SearchHit> query(String sql,Scope s){return jdbc.query(sql,s.params,(rs,row)->new SearchHit(rs.getString("type"),rs.getString("id"),rs.getString("patient_id"),rs.getString("title"),rs.getString("subtitle"),rs.getString("status"),toInstant(rs.getTimestamp("occurred_at")),rs.getDouble("relevance"),rs.getString("route")));}
    private Scope scope(){
        String role=actor.require().getAuthorities().iterator().next().getAuthority();var params=new MapSqlParameterSource().addValue("userId",actor.id());
        if(role.equals("ROLE_ADMIN"))return new Scope("true",params,true,false);
        if(role.equals("ROLE_PATIENT"))return new Scope("p.portal_user_id=:userId",params,false,true);
        Set<UUID> orgs=access.accessibleOrganizationIds();params.addValue("orgs",orgs.isEmpty()?List.of(UUID.randomUUID()):orgs);
        return new Scope("p.managing_organization_id in (:orgs)",params,false,false);
    }
    private Instant toInstant(Timestamp value){return value==null?null:value.toInstant();}
    private record Scope(String patientPredicate,MapSqlParameterSource params,boolean admin,boolean patient){}
}
