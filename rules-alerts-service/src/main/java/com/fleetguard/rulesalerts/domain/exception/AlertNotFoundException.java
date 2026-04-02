package com.fleetguard.rulesalerts.domain.exception;

import java.util.UUID;

public class AlertNotFoundException extends RuntimeException {
    public AlertNotFoundException(UUID alertId) {
        super("Alerta no encontrada con ID: " + alertId);
    }
}