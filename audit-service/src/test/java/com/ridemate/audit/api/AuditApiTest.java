package com.ridemate.audit.api;

import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.repository.AuditEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Ensure you have application-test.properties or similar if needed, or rely on
                        // H2 default
class AuditApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditEntryRepository auditEntryRepository;

    @BeforeEach
    void setUp() {
        auditEntryRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveAuditEntry() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();

        String json = """
                {
                    "userId": "%s",
                    "actionType": "LOGIN",
                    "entityType": "USER",
                    "entityId": "%s",
                    "description": "User logged in successfully"
                }
                """.formatted(userId, entityId);

        // Create
        mockMvc.perform(post("/audit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").value(userId.toString()));

        // Retrieve
        mockMvc.perform(get("/audit/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].actionType").value("LOGIN"));
    }
}
