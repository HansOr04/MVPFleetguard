package com.fleetguard.fleet.domain.exception;

public class MissingRecordedByException extends RuntimeException {

    public MissingRecordedByException() {
        super("El campo 'Registrado por' es obligatorio");
    }
}
