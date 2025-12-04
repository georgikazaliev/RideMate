package com.ridemate.audit.service;

import com.ridemate.audit.dto.AuditEntryViewDTO;
import com.ridemate.audit.dto.CreateAuditEntryDTO;
import com.ridemate.audit.entity.AuditEntry;
import com.ridemate.audit.exception.AuditEntryNotFoundException;
import com.ridemate.audit.repository.AuditEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditEntryRepository auditEntryRepository;

    public AuditService(AuditEntryRepository auditEntryRepository) {
        this.auditEntryRepository = auditEntryRepository;
    }

    @Transactional
    @CacheEvict(value = "audit_entries", key = "#dto.userId")
    public AuditEntryViewDTO createEntry(CreateAuditEntryDTO dto) {
        logger.info("[AuditService] Creating new audit entry - User: {}, Action: {}, EntityType: {}", dto.getUserId(),
                dto.getActionType(), dto.getEntityType());
        AuditEntry entry = new AuditEntry(
                dto.getUserId(),
                dto.getActionType(),
                dto.getEntityType(),
                dto.getEntityId(),
                dto.getDescription());
        AuditEntry savedEntry = auditEntryRepository.save(entry);
        return mapToViewDTO(savedEntry);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "audit_entries", key = "#userId")
    public List<AuditEntryViewDTO> getEntriesForUser(UUID userId) {
        logger.info("[AuditService] Retrieving audit log entries for user ID: {}", userId);
        return auditEntryRepository.findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(this::mapToViewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "audit_entries", allEntries = true)

    public void deleteEntry(UUID id) {
        logger.info("[AuditService] Attempting to delete audit entry with ID: {}", id);
        AuditEntry entry = auditEntryRepository.findById(id)
                .orElseThrow(() -> new AuditEntryNotFoundException("Audit entry not found with id: " + id));
        auditEntryRepository.delete(entry);
    }

    @Transactional
    @CacheEvict(value = "audit_entries", key = "#userId")
    public void deleteEntriesForUser(UUID userId) {
        logger.info("[AuditService] Clearing all audit log entries for user ID: {}", userId);
        auditEntryRepository.deleteByUserId(userId);
    }

    @Transactional
    @CacheEvict(value = "audit_entries", allEntries = true)
    public void purgeOldEntries() {
        logger.info("[AuditService] Starting purge operation for old audit entries (older than 30 days)");
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        auditEntryRepository.deleteOlderThan(cutoff);
    }

    @Transactional(readOnly = true)
    public long countTotalEntries() {
        logger.info("[AuditService] Counting total audit entries.");
        return auditEntryRepository.count();
    }

    private AuditEntryViewDTO mapToViewDTO(AuditEntry entry) {
        return new AuditEntryViewDTO(
                entry.getId(),
                entry.getUserId(),
                entry.getActionType(),
                entry.getEntityType(),
                entry.getEntityId(),
                entry.getDescription(),
                entry.getTimestamp());
    }
}
