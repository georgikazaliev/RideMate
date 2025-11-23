package com.ridemate.audit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ridemate.audit.dto.AuditEntryViewDTO;
import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController auditController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createEntry_ShouldReturnCreated() throws Exception {
        CreateAuditEntryDTO createDto = new CreateAuditEntryDTO();
        createDto.setUserId(UUID.randomUUID());
        createDto.setActionType("LOGIN");
        createDto.setEntityType("USER");
        createDto.setEntityId(UUID.randomUUID());
        createDto.setDescription("User logged in");

        AuditEntryViewDTO viewDto = new AuditEntryViewDTO(
                UUID.randomUUID(),
                createDto.getUserId(),
                createDto.getActionType(),
                createDto.getEntityType(),
                createDto.getEntityId(),
                createDto.getDescription(),
                LocalDateTime.now());

        when(auditService.createEntry(any(CreateAuditEntryDTO.class))).thenReturn(viewDto);

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.actionType").value("LOGIN"));

        verify(auditService, times(1)).createEntry(any(CreateAuditEntryDTO.class));
    }

    @Test
    void getEntriesForUser_ShouldReturnList() throws Exception {
        UUID userId = UUID.randomUUID();
        AuditEntryViewDTO viewDto = new AuditEntryViewDTO(
                UUID.randomUUID(),
                userId,
                "LOGIN",
                "USER",
                UUID.randomUUID(),
                "User logged in",
                LocalDateTime.now());

        when(auditService.getEntriesForUser(userId)).thenReturn(Collections.singletonList(viewDto));

        mockMvc.perform(get("/audit/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        verify(auditService, times(1)).getEntriesForUser(userId);
    }

    @Test
    void deleteEntry_ShouldReturnNoContent() throws Exception {
        UUID entryId = UUID.randomUUID();
        doNothing().when(auditService).deleteEntry(entryId);

        mockMvc.perform(delete("/audit/{entryId}", entryId))
                .andExpect(status().isNoContent());

        verify(auditService, times(1)).deleteEntry(entryId);
    }

    @Test
    void purgeOldEntries_ShouldReturnNoContent() throws Exception {
        doNothing().when(auditService).purgeOldEntries();

        mockMvc.perform(delete("/audit/purge"))
                .andExpect(status().isNoContent());

        verify(auditService, times(1)).purgeOldEntries();
    }
}
