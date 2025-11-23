package com.ridemate.audit.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditEntryViewDTO {
    private UUID id;
    private UUID userId;
    private String actionType;
    private String entityType;
    private UUID entityId;
    private String description;
    private LocalDateTime timestamp;

    public AuditEntryViewDTO() {
    }

    public AuditEntryViewDTO(UUID id, UUID userId, String actionType, String entityType, UUID entityId,
            String description, LocalDateTime timestamp) {
        this.id = id;
        this.userId = userId;
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.timestamp = timestamp;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
