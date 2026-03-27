package com.fleetguard.rulesalerts.domain.exception;

public class MaintenanceRuleNotFoundException extends RuntimeException {

    public MaintenanceRuleNotFoundException(String message) {
        super(message);
    }
}
