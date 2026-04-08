package com.fleetguard.rulesalerts.domain.exception;

public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(String plate) {
        super("Vehículo no encontrado con placa: " + plate);
    }
}
