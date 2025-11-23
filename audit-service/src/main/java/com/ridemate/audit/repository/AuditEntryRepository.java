package com.ridemate.audit.repository;

import com.ridemate.audit.entity.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditEntryRepository extends JpaRepository<AuditEntry, UUID> {
    List<AuditEntry> findByUserIdOrderByTimestampDesc(UUID userId);

    @Modifying
    @Query("DELETE FROM AuditEntry a WHERE a.timestamp < :cutoffDate")
    void deleteOlderThan(LocalDateTime cutoffDate);

    void deleteByUserId(UUID userId);
}
