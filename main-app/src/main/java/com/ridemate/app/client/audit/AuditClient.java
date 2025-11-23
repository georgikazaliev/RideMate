package com.ridemate.app.client.audit;

import com.ridemate.app.client.audit.dto.AuditEntryViewDTO;
import com.ridemate.app.client.audit.dto.CreateAuditEntryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "audit-service", url = "http://localhost:8083/audit")
public interface AuditClient {

    @PostMapping
    ResponseEntity<AuditEntryViewDTO> createEntry(@RequestBody CreateAuditEntryDTO dto);

    @org.springframework.web.bind.annotation.GetMapping("/user/{userId}")
    ResponseEntity<java.util.List<AuditEntryViewDTO>> getEntriesForUser(
            @org.springframework.web.bind.annotation.PathVariable("userId") java.util.UUID userId);

    @org.springframework.web.bind.annotation.DeleteMapping("/user/{userId}")
    ResponseEntity<Void> deleteEntriesForUser(
            @org.springframework.web.bind.annotation.PathVariable("userId") java.util.UUID userId);
}
