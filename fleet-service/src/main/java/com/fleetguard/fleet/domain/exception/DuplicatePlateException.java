package com.fleetguard.fleet.domain.exception;

public class DuplicatePlateException extends RuntimeException {

    public DuplicatePlateException(String plate) {
        super("Ya existe un vehículo con la placa '" + plate + "'");
    }
}