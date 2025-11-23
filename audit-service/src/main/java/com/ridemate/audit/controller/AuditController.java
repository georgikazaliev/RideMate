package com.ridemate.audit.controller;

import com.ridemate.audit.dto.AuditEntryViewDTO;
import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.service.AuditService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private static final Logger logger = LoggerFactory.getLogger(AuditController.class);

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<AuditEntryViewDTO> createEntry(@Valid @RequestBody CreateAuditEntryDTO dto) {
        logger.info("POST /audit - Creating audit entry for user: {}, action: {}", dto.getUserId(),
                dto.getActionType());
        AuditEntryViewDTO created = auditService.createEntry(dto);
        logger.info("POST /audit - Successfully created audit entry with id: {}", created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditEntryViewDTO>> getEntriesForUser(@PathVariable UUID userId) {
        logger.info("GET /audit/user/{} - Fetching audit entries for user", userId);
        List<AuditEntryViewDTO> entries = auditService.getEntriesForUser(userId);
        logger.info("GET /audit/user/{} - Retrieved {} entries", userId, entries.size());
        return ResponseEntity.ok(entries);
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable UUID entryId) {
        logger.info("DELETE /audit/{} - Deleting audit entry", entryId);
        auditService.deleteEntry(entryId);
        logger.info("DELETE /audit/{} - Successfully deleted audit entry", entryId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Void> purgeOldEntries() {
        logger.info("DELETE /audit/purge - Purging old audit entries");
        auditService.purgeOldEntries();
        logger.info("DELETE /audit/purge - Successfully purged old audit entries");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteEntriesForUser(@PathVariable UUID userId) {
        logger.info("DELETE /audit/user/{} - Deleting all entries for user", userId);
        auditService.deleteEntriesForUser(userId);
        logger.info("DELETE /audit/user/{} - Successfully deleted all entries for user", userId);
        return ResponseEntity.noContent().build();
    }
}
