package com.fleetguard.fleet.domain.exception;

import java.util.UUID;

public class VehicleTypeNotFoundException extends RuntimeException {

    public VehicleTypeNotFoundException(UUID vehicleTypeId) {
        super("Tipo de vehículo no encontrado con ID: " + vehicleTypeId);
    }
}