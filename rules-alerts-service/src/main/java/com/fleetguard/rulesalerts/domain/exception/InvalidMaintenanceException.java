package com.fleetguard.rulesalerts.domain.exception;

public class InvalidMaintenanceException extends RuntimeException {

    public InvalidMaintenanceException(String message) {
        super(message);
    }
}