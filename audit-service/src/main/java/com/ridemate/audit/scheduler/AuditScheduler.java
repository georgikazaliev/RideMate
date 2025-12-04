package com.ridemate.audit.scheduler;

import com.ridemate.audit.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AuditScheduler {

    private static final Logger logger = LoggerFactory.getLogger(AuditScheduler.class);

    private final AuditService auditService;

    public AuditScheduler(AuditService auditService) {
        this.auditService = auditService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeOldEntries() {
        logger.info("[Scheduler] Daily purge job triggered - removing old audit entries");
        auditService.purgeOldEntries();
        logger.info("[Scheduler] Daily purge job completed successfully");
    }

    @Scheduled(fixedRate = 3600000)
    public void computeStatistics() {
        logger.info("[Scheduler] Hourly statistics computation task initiated");
        long totalEntries = auditService.countTotalEntries();
        logger.info("[Scheduler] Total audit entries: {}", totalEntries);
        logger.info("[Scheduler] Hourly statistics computation completed");
    }
}
