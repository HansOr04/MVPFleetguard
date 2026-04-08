package com.fleetguard.fleet.domain.exception;

public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(String plate) {
        super("Vehículo no encontrado con la placa: " + plate);
    }
}