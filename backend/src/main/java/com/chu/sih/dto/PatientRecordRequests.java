package com.chu.sih.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public final class PatientRecordRequests {
    private PatientRecordRequests() {}

    public record IdentifierCreate(
            @NotBlank @Size(max=255) String systemUri,
            @NotBlank @Size(max=128) String value,
            @NotBlank @Size(max=40) String identifierType,
            LocalDate validFrom,
            LocalDate validUntil) {
        @AssertTrue(message="La fin de validite doit suivre le debut")
        public boolean hasValidPeriod(){return validUntil==null||validFrom==null||!validUntil.isBefore(validFrom);}
    }

    public record ContactCreate(
            @NotBlank @Size(max=180) String fullName,
            @NotBlank @Size(max=60) String relationshipCode,
            @Size(max=32) String phone,
            @Email String email,
            boolean emergencyContact,
            boolean legalRepresentative) {}

    public record AllergyCreate(
            @NotBlank @Size(max=100) String substanceCode,
            @NotBlank @Size(max=180) String substanceDisplay,
            @NotBlank @Pattern(regexp="LOW|HIGH|UNABLE_TO_ASSESS") String criticality,
            String reaction) {}

    public record ConditionCreate(
            @Size(max=120) String codeSystem,
            @NotBlank @Size(max=80) String code,
            @NotBlank @Size(max=255) String display,
            Instant onsetAt) {}

    public record EpisodeCreate(
            UUID careTeamId,
            @NotNull Instant startedAt) {}
}
