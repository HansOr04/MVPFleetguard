package com.fleetguard.fleet.domain.exception;

public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(String plate) {
        super("Vehicle not found with plate: " + plate);
    }
}
