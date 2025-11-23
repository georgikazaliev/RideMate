package com.ridemate.audit.controller;

import com.ridemate.audit.dto.AuditEntryViewDTO;
import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.service.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
class AuditControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditService auditService;

    @Test
    void createEntry_ShouldReturnCreated() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateAuditEntryDTO dto = new CreateAuditEntryDTO();
        dto.setUserId(userId);
        dto.setActionType("TEST");
        dto.setEntityType("TEST_ENTITY");
        dto.setEntityId(UUID.randomUUID());

        AuditEntryViewDTO viewDTO = new AuditEntryViewDTO(
                UUID.randomUUID(), userId, "TEST", "TEST_ENTITY", dto.getEntityId(), null, LocalDateTime.now());

        given(auditService.createEntry(any(CreateAuditEntryDTO.class))).willReturn(viewDTO);

        String json = """
                {
                    "userId": "%s",
                    "actionType": "TEST",
                    "entityType": "TEST_ENTITY",
                    "entityId": "%s"
                }
                """.formatted(userId, dto.getEntityId());

        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.actionType").value("TEST"));
    }

    @Test
    void getEntriesForUser_ShouldReturnList() throws Exception {
        UUID userId = UUID.randomUUID();
        AuditEntryViewDTO viewDTO = new AuditEntryViewDTO(
                UUID.randomUUID(), userId, "TEST", "TEST_ENTITY", UUID.randomUUID(), null, LocalDateTime.now());

        given(auditService.getEntriesForUser(userId)).willReturn(Arrays.asList(viewDTO));

        mockMvc.perform(get("/audit/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }
}
