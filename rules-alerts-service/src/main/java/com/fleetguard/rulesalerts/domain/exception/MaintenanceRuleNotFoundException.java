package com.fleetguard.rulesalerts.domain.exception;

/**
 * Excepción de dominio lanzada cuando no se encuentra una regla de mantenimiento.
 * No depende de ningún framework — es dominio puro.
 */
public class MaintenanceRuleNotFoundException extends RuntimeException {

    public MaintenanceRuleNotFoundException(String message) {
        super(message);
    }
}
