package com.ridemate.app.client.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditEntryViewDTO {
    private UUID id;
    private UUID userId;
    private String actionType;
    private String entityType;
    private UUID entityId;
    private String description;
    private LocalDateTime timestamp;
}
