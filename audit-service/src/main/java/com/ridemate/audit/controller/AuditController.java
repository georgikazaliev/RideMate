package com.ridemate.audit.controller;

import com.ridemate.audit.dto.AuditEntryViewDTO;
import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.service.AuditService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<AuditEntryViewDTO> createEntry(@Valid @RequestBody CreateAuditEntryDTO dto) {
        AuditEntryViewDTO created = auditService.createEntry(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditEntryViewDTO>> getEntriesForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(auditService.getEntriesForUser(userId));
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable UUID entryId) {
        auditService.deleteEntry(entryId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/purge")
    public ResponseEntity<Void> purgeOldEntries() {
        auditService.purgeOldEntries();
        return ResponseEntity.noContent().build();
    }
}
