package com.chu.sih.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ClinicalRequests {
    private ClinicalRequests() {}

    public record PatientCreate(
            @NotBlank @Size(max=64) String medicalRecordNumber,
            @Size(max=64) String nationalIdentifier,
            @NotBlank @Size(max=100) String familyName,
            @NotBlank @Size(max=100) String givenName,
            @NotNull @PastOrPresent LocalDate birthDate,
            @NotBlank @Pattern(regexp="FEMALE|MALE|OTHER|UNKNOWN") String administrativeGender,
            @Size(max=8) String bloodGroup,
            @Size(max=32) String phone,
            @Email String email,
            String preferredLanguage) {}

    public record PrescriptionCreate(
            @NotNull UUID patientId,
            UUID episodeId,
            UUID protocolId,
            @NotBlank String indicationCode,
            @NotBlank String indicationDisplay,
            String asfaCategory,
            @NotBlank String modality,
            @Pattern(regexp="ROUTINE|URGENT|STAT") String priority,
            @Min(1) int sessionsPlanned,
            String frequencyText,
            @Positive BigDecimal targetVolumeMl,
            String replacementFluid,
            String anticoagulant,
            @Positive BigDecimal anticoagulantRatio,
            String calciumProphylaxis,
            String premedication,
            String vascularAccessPlan,
            String clinicalInstructions) {}

    public record PrescriptionRequirementCreate(
            @NotBlank @Size(max=40) String requirementType,
            @NotBlank @Size(max=100) String code,
            @NotBlank @Size(max=255) String display,
            @Size(max=40) String timing,
            boolean mandatory,
            String thresholdDefinition) {}

    public record AppointmentCreate(
            @NotNull UUID patientId,
            UUID prescriptionId,
            UUID locationId,
            UUID equipmentId,
            @NotNull Instant startsAt,
            @NotNull Instant endsAt,
            String reason) {}

    public record AppointmentStaffCreate(@NotNull Long userId,@NotBlank @Size(max=60) String participationRole) {}
    public record AppointmentTransition(@NotBlank @Pattern(regexp="PROPOSED|BOOKED|ARRIVED|FULFILLED|CANCELLED|NO_SHOW") String status,String reason) {}

    public record SessionCreate(
            @NotNull UUID patientId,
            @NotNull UUID prescriptionId,
            UUID appointmentId,
            UUID equipmentId,
            UUID locationId,
            @Min(1) int sequenceNumber,
            @Positive BigDecimal plannedVolumeMl,
            String vascularAccess) {}

    public record Transition(@NotBlank String targetStatus, String reason, String clinicalSummary) {}

    public record ObservationCreate(
            @NotBlank String observationCode,
            String codeSystem,
            BigDecimal valueNumeric,
            String valueText,
            String unitUcum,
            @Pattern(regexp="MANUAL|DEVICE|IMPORTED") String source,
            UUID deviceId,
            @NotNull Instant observedAt) {
        @AssertTrue(message="Une valeur numérique ou textuelle est obligatoire")
        public boolean hasValue(){ return valueNumeric != null || (valueText != null && !valueText.isBlank()); }
    }

    public record SessionMetricsUpdate(
            @PositiveOrZero BigDecimal actualProcessedVolumeMl,
            @PositiveOrZero BigDecimal replacementVolumeMl,
            @PositiveOrZero BigDecimal anticoagulantVolumeMl,
            BigDecimal fluidBalanceMl) {}

    public record SessionAlarmCreate(
            UUID deviceId,
            @NotBlank @Size(max=80) String alarmCode,
            @NotBlank @Pattern(regexp="LOW|MODERATE|HIGH|CRITICAL") String severity,
            @NotBlank @Size(max=2000) String message) {}

    public record AlarmResolution(@NotBlank @Size(max=2000) String actionTaken) {}

    public record ConsumableUse(
            @NotNull UUID inventoryLotId,
            @NotNull @Positive BigDecimal quantity) {}

    public record BiologicProductCreate(
            @NotBlank @Size(max=160) String productIdentifier,
            @NotBlank @Size(max=80) String productType,
            @Size(max=8) String bloodGroup,
            @Positive BigDecimal volumeMl,
            Instant expiresAt,
            @NotBlank @Size(max=40) String disposition) {}

    public record IncidentCreate(
            UUID patientId,
            UUID sessionId,
            UUID equipmentId,
            @NotBlank String category,
            @Pattern(regexp="LOW|MODERATE|HIGH|CRITICAL") String severity,
            @NotNull Instant occurredAt,
            @NotBlank String description,
            String immediateAction,
            String causality,
            boolean reportable) {}

    public record IncidentTransition(
            @NotBlank @Pattern(regexp="OPEN|UNDER_REVIEW|ACTION_REQUIRED|CLOSED") String status,
            String review) {}

    public record CorrectiveActionCreate(
            @NotBlank @Pattern(regexp="CORRECTIVE|PREVENTIVE") String actionType,
            @NotBlank @Size(max=4000) String description,
            @NotNull Long ownerId,
            @NotNull @Future Instant dueAt) {}

    public record CorrectiveActionComplete(@NotBlank @Size(max=4000) String effectivenessReview) {}

    public record EquipmentCreate(
            @NotBlank String assetNumber,
            String udi,
            @NotBlank String manufacturer,
            @NotBlank String model,
            @NotBlank String serialNumber,
            @NotBlank String equipmentType,
            UUID locationId,
            LocalDate commissionedOn,
            Instant nextMaintenanceAt,
            String firmwareVersion) {}

    public record MaintenanceWorkOrderCreate(
            @NotNull UUID equipmentId,
            @NotBlank @Pattern(regexp="PREVENTIVE|CORRECTIVE|CALIBRATION|SAFETY_CHECK") String maintenanceType,
            @NotBlank @Pattern(regexp="LOW|NORMAL|HIGH|CRITICAL") String priority,
            @NotBlank @Size(max=4000) String description,
            Long assignedTo,
            Instant scheduledAt) {}

    public record MaintenanceWorkOrderComplete(
            @NotBlank @Size(max=4000) String completionNotes,
            Instant nextDueAt) {}

    public record InventoryItemCreate(
            @NotBlank @Size(max=80) String sku,
            @NotBlank @Size(max=255) String name,
            @NotBlank @Size(max=60) String itemType,
            @NotBlank @Size(max=32) String unit,
            @PositiveOrZero BigDecimal minimumStock) {}

    public record InventoryLotCreate(
            @NotNull UUID itemId,
            @NotBlank @Size(max=120) String lotNumber,
            LocalDate expiresOn,
            @NotNull @Positive BigDecimal quantityAvailable,
            UUID locationId) {}

    public record InventoryAdjustment(
            @NotNull @Positive BigDecimal quantity,
            @NotBlank @Pattern(regexp="ADJUSTMENT|DISPOSAL|QUARANTINE|RELEASE") String movementType,
            @NotBlank @Size(max=1000) String reason) {}

    public record DocumentMetadata(
            @NotNull UUID patientId,
            @NotBlank @Size(max=60) String documentType,
            @NotBlank @Size(max=255) String title,
            @Size(max=4000) String description,
            @Pattern(regexp="PATIENT_VISIBLE|CLINICAL|RESTRICTED") String confidentiality) {}

    public record AiDraftRequest(
            @NotNull UUID patientId,
            UUID sessionId,
            @NotBlank @Pattern(regexp="HANDOFF_DRAFT|SESSION_SUMMARY|DATA_QUALITY|PATIENT_EXPLANATION") String assistanceType,
            @NotBlank @Size(max=1000) String purpose,
            @Size(max=4000) String clinicianInput) {}

    public record AiReviewRequest(
            @NotBlank @Pattern(regexp="ACCEPTED|REJECTED") String status,
            @NotBlank @Size(max=2000) String reviewNote) {}

    public record PatientReportedOutcomeCreate(
            UUID sessionId,
            @NotBlank @Size(max=80) String questionnaireCode,
            @NotEmpty Map<String, Object> response,
            BigDecimal score) {}

    public record NotificationPreferenceUpdate(
            boolean inAppEnabled,
            boolean emailEnabled,
            boolean smsEnabled,
            LocalTime quietHoursStart,
            LocalTime quietHoursEnd,
            @NotBlank @Size(max=60) String timezone) {}

    public record LabItem(@NotBlank String loincCode, @NotBlank String display, String specimenType) {}
    public record LabOrderCreate(
            @NotNull UUID patientId,
            UUID prescriptionId,
            @Pattern(regexp="ROUTINE|URGENT|STAT") String priority,
            Instant requiredAt,
            String clinicalContext,
            @NotEmpty List<@Valid LabItem> items) {}

    public record LabResultCreate(
            @NotNull UUID orderItemId,
            @NotNull UUID specimenId,
            @NotBlank String loincCode,
            BigDecimal valueNumeric,
            String valueText,
            String unitUcum,
            BigDecimal referenceLow,
            BigDecimal referenceHigh,
            String interpretation,
            boolean critical,
            @NotNull Instant measuredAt) {
        @AssertTrue(message="Une valeur numérique ou textuelle est obligatoire")
        public boolean hasValue(){ return valueNumeric != null || (valueText != null && !valueText.isBlank()); }
    }

    public record SpecimenCreate(
            @NotNull UUID orderId,
            @NotBlank @Size(max=80) String specimenType,
            @Size(max=80) String accessionNumber,
            @Size(max=120) String barcode) {}

    public record CriticalResultAcknowledgementCreate(@NotBlank @Size(max=2000) String actionTaken) {}

    public record ConsentCreate(
            @NotNull UUID patientId,
            @NotBlank String consentType,
            @NotBlank String scopeCode,
            Instant validUntil) {}

    public record TaskCreate(
            UUID patientId,
            UUID sessionId,
            @NotBlank String taskType,
            @Pattern(regexp="ROUTINE|URGENT|STAT") String priority,
            @NotBlank String description,
            Long ownerId,
            String ownerRole,
            Instant dueAt) {}

    public record ClinicalThreadCreate(
            UUID patientId,
            UUID sessionId,
            @NotBlank @Size(max=255) String subject,
            @Pattern(regexp="ROUTINE|URGENT|STAT") String priority,
            @NotEmpty List<@NotNull Long> participantIds,
            @NotBlank @Size(max=8000) String initialMessage) {
        @AssertTrue(message="Le fil doit etre lie a un patient ou une seance")
        public boolean hasContext(){return patientId != null || sessionId != null;}
    }

    public record ClinicalMessageCreate(
            UUID replyToId,
            @Pattern(regexp="TEXT|HANDOFF|ALERT|DECISION") String messageType,
            @Pattern(regexp="ROUTINE|URGENT|STAT") String urgency,
            @NotBlank @Size(max=8000) String body) {}
}
