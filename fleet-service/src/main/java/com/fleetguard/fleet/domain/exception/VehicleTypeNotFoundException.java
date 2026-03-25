package com.fleetguard.fleet.domain.exception;

import java.util.UUID;

public class VehicleTypeNotFoundException extends RuntimeException {

    public VehicleTypeNotFoundException(UUID vehicleTypeId) {
        super("Vehicle type not found with id: " + vehicleTypeId);
    }
}
