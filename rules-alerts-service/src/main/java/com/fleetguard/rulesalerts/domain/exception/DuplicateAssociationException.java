package com.fleetguard.rulesalerts.domain.exception;

public class DuplicateAssociationException extends RuntimeException {

    public DuplicateAssociationException(String message) {
        super(message);
    }
}
