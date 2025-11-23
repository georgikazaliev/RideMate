package com.ridemate.audit.service;

import com.ridemate.audit.domain.AuditEntry;
import com.ridemate.audit.dto.AuditEntryViewDTO;
import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.exception.AuditEntryNotFoundException;
import com.ridemate.audit.repository.AuditEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    private CreateAuditEntryDTO createDTO;
    private AuditEntry auditEntry;
    private UUID userId;
    private UUID entryId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        entryId = UUID.randomUUID();

        createDTO = new CreateAuditEntryDTO();
        createDTO.setUserId(userId);
        createDTO.setActionType("LOGIN");
        createDTO.setEntityType("USER");
        createDTO.setEntityId(userId);
        createDTO.setDescription("User logged in");

        auditEntry = new AuditEntry(
                userId,
                "LOGIN",
                "USER",
                userId,
                "User logged in");
        auditEntry.setId(entryId);
    }

    @Test
    void createEntry_ShouldReturnViewDTO() {
        when(auditEntryRepository.save(any(AuditEntry.class))).thenReturn(auditEntry);

        AuditEntryViewDTO result = auditService.createEntry(createDTO);

        assertNotNull(result);
        assertEquals(entryId, result.getId());
        assertEquals(userId, result.getUserId());
        assertEquals("LOGIN", result.getActionType());
        verify(auditEntryRepository, times(1)).save(any(AuditEntry.class));
    }

    @Test
    void getEntriesForUser_ShouldReturnListOfDTOs() {
        when(auditEntryRepository.findByUserIdOrderByTimestampDesc(userId)).thenReturn(Arrays.asList(auditEntry));

        List<AuditEntryViewDTO> results = auditService.getEntriesForUser(userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(entryId, results.get(0).getId());
        verify(auditEntryRepository, times(1)).findByUserIdOrderByTimestampDesc(userId);
    }

    @Test
    void deleteEntry_ShouldDelete_WhenFound() {
        when(auditEntryRepository.findById(entryId)).thenReturn(Optional.of(auditEntry));

        auditService.deleteEntry(entryId);

        verify(auditEntryRepository, times(1)).findById(entryId);
        verify(auditEntryRepository, times(1)).delete(auditEntry);
    }

    @Test
    void deleteEntry_ShouldThrowException_WhenNotFound() {
        when(auditEntryRepository.findById(entryId)).thenReturn(Optional.empty());

        assertThrows(AuditEntryNotFoundException.class, () -> auditService.deleteEntry(entryId));

        verify(auditEntryRepository, times(1)).findById(entryId);
        verify(auditEntryRepository, never()).delete(any());
    }

    @Test
    void purgeOldEntries_ShouldCallRepository() {
        auditService.purgeOldEntries();

        verify(auditEntryRepository, times(1)).deleteOlderThan(any(LocalDateTime.class));
    }
}
