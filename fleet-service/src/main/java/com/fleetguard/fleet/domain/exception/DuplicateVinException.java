package com.fleetguard.fleet.domain.exception;

public class DuplicateVinException extends RuntimeException {

    public DuplicateVinException(String vin) {
        super("El VIN '" + vin + "' ya existe en el sistema");
    }
}