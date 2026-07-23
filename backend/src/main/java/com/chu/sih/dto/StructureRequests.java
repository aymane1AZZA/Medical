package com.chu.sih.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;

public final class StructureRequests {
    private StructureRequests() {}
    public record OrganizationCreate(@NotBlank @Size(max=40) String code,@NotBlank @Size(max=180) String name,@NotBlank @Size(max=40) String organizationType){}
    public record LocationCreate(@NotNull UUID organizationId,UUID parentId,@NotBlank @Size(max=40) String code,@NotBlank @Size(max=180) String name,@NotBlank @Size(max=40) String locationType){}
    public record CareTeamCreate(@NotNull UUID organizationId,@NotBlank @Size(max=180) String name){}
    public record CareTeamMemberCreate(@NotNull Long userId,@NotBlank @Size(max=60) String memberRole,@Future Instant validUntil){}
}
