package com.ridemate.audit.exception;

public class AuditEntryNotFoundException extends RuntimeException {
    public AuditEntryNotFoundException(String message) {
        super(message);
    }
}
