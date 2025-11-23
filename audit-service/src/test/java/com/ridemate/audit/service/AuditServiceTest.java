package com.ridemate.audit.service;

import com.ridemate.audit.dto.AuditEntryViewDTO;
import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.entity.AuditEntry;
import com.ridemate.audit.exception.AuditEntryNotFoundException;
import com.ridemate.audit.repository.AuditEntryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditEntryRepository auditEntryRepository;

    @InjectMocks
    private AuditService auditService;

    @Test
    void createEntry_ShouldSaveAndReturnDto() {
        CreateAuditEntryDTO createDto = new CreateAuditEntryDTO();
        createDto.setUserId(UUID.randomUUID());
        createDto.setActionType("LOGIN");
        createDto.setEntityType("USER");
        createDto.setEntityId(UUID.randomUUID());
        createDto.setDescription("User logged in");

        AuditEntry savedEntry = new AuditEntry(
                createDto.getUserId(),
                createDto.getActionType(),
                createDto.getEntityType(),
                createDto.getEntityId(),
                createDto.getDescription());
        savedEntry.setId(UUID.randomUUID());
        savedEntry.setTimestamp(LocalDateTime.now());

        when(auditEntryRepository.save(any(AuditEntry.class))).thenReturn(savedEntry);

        AuditEntryViewDTO result = auditService.createEntry(createDto);

        assertNotNull(result);
        assertEquals(createDto.getActionType(), result.getActionType());
        verify(auditEntryRepository, times(1)).save(any(AuditEntry.class));
    }

    @Test
    void getEntriesForUser_ShouldReturnList() {
        UUID userId = UUID.randomUUID();
        AuditEntry entry = new AuditEntry(
                userId,
                "LOGIN",
                "USER",
                UUID.randomUUID(),
                "User logged in");
        entry.setId(UUID.randomUUID());
        entry.setTimestamp(LocalDateTime.now());

        when(auditEntryRepository.findByUserIdOrderByTimestampDesc(userId))
                .thenReturn(Collections.singletonList(entry));

        List<AuditEntryViewDTO> result = auditService.getEntriesForUser(userId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(userId, result.get(0).getUserId());
        verify(auditEntryRepository, times(1)).findByUserIdOrderByTimestampDesc(userId);
    }

    @Test
    void deleteEntry_ShouldDelete_WhenFound() {
        UUID entryId = UUID.randomUUID();
        AuditEntry entry = new AuditEntry();
        entry.setId(entryId);

        when(auditEntryRepository.findById(entryId)).thenReturn(Optional.of(entry));
        doNothing().when(auditEntryRepository).delete(entry);

        auditService.deleteEntry(entryId);

        verify(auditEntryRepository, times(1)).findById(entryId);
        verify(auditEntryRepository, times(1)).delete(entry);
    }

    @Test
    void deleteEntry_ShouldThrowException_WhenNotFound() {
        UUID entryId = UUID.randomUUID();
        when(auditEntryRepository.findById(entryId)).thenReturn(Optional.empty());

        assertThrows(AuditEntryNotFoundException.class, () -> auditService.deleteEntry(entryId));

        verify(auditEntryRepository, times(1)).findById(entryId);
        verify(auditEntryRepository, never()).delete(any());
    }

    @Test
    void purgeOldEntries_ShouldDeleteOlderThan() {
        doNothing().when(auditEntryRepository).deleteOlderThan(any(LocalDateTime.class));

        auditService.purgeOldEntries();

        verify(auditEntryRepository, times(1)).deleteOlderThan(any(LocalDateTime.class));
    }
}
