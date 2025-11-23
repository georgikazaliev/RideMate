package com.ridemate.audit.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AuditEntryViewDTOTest {

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String actionType = "LOGIN";
        String entityType = "USER";
        UUID entityId = UUID.randomUUID();
        String description = "User logged in";
        LocalDateTime timestamp = LocalDateTime.now();

        AuditEntryViewDTO dto = new AuditEntryViewDTO(id, userId, actionType, entityType, entityId, description,
                timestamp);

        assertEquals(id, dto.getId());
        assertEquals(userId, dto.getUserId());
        assertEquals(actionType, dto.getActionType());
        assertEquals(entityType, dto.getEntityType());
        assertEquals(entityId, dto.getEntityId());
        assertEquals(description, dto.getDescription());
        assertEquals(timestamp, dto.getTimestamp());

        UUID newId = UUID.randomUUID();
        dto.setId(newId);
        assertEquals(newId, dto.getId());

        dto.setUserId(userId);
        assertEquals(userId, dto.getUserId());

        dto.setActionType("LOGOUT");
        assertEquals("LOGOUT", dto.getActionType());

        dto.setEntityType("RIDE");
        assertEquals("RIDE", dto.getEntityType());

        dto.setEntityId(entityId);
        assertEquals(entityId, dto.getEntityId());

        dto.setDescription("User logged out");
        assertEquals("User logged out", dto.getDescription());

        dto.setTimestamp(timestamp);
        assertEquals(timestamp, dto.getTimestamp());
    }

    @Test
    void testNoArgsConstructor() {
        AuditEntryViewDTO dto = new AuditEntryViewDTO();
        assertNull(dto.getId());
    }
}
