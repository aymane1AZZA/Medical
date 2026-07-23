package com.chu.sih.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public final class ProtocolRequests {
    private ProtocolRequests() {}
    public record ProtocolCreate(
            @NotBlank @Size(max=60) String code,
            @NotBlank @Size(max=255) String name,
            @NotBlank @Size(max=60) String modality,
            @NotBlank String definition,
            LocalDate effectiveFrom) {}
}
