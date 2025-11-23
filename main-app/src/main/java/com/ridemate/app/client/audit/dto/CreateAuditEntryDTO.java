package com.ridemate.app.client.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAuditEntryDTO {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotBlank(message = "Action Type is required")
    private String actionType;

    @NotBlank(message = "Entity Type is required")
    private String entityType;

    @NotNull(message = "Entity ID is required")
    private UUID entityId;

    private String description;
}
