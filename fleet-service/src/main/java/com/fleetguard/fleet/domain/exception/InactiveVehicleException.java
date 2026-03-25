package com.fleetguard.fleet.domain.exception;

import java.util.UUID;

public class InactiveVehicleException extends RuntimeException {

    public InactiveVehicleException(UUID vehicleId) {
        super("Vehicle " + vehicleId + " is not active");
    }
}
