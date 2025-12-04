package com.ridemate.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class AuditServiceApplication {

    private static final Logger logger = LoggerFactory.getLogger(AuditServiceApplication.class);

    public static void main(String[] args) {
        logger.info("[Application] Starting RideMate Audit Service...");
        SpringApplication.run(AuditServiceApplication.class, args);
        logger.info("[Application] RideMate Audit Service started successfully");
    }

}
