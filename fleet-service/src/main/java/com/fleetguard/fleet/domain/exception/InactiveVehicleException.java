package com.fleetguard.fleet.domain.exception;

import java.util.UUID;

public class InactiveVehicleException extends RuntimeException {

    public InactiveVehicleException(UUID vehicleId) {
        super("El vehículo " + vehicleId + " no está activo");
    }
}