package com.fleetguard.fleet.domain.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTypeNotFoundExceptionTest {

    @Test
    void shouldReturnCorrectMessage() {
        UUID vehicleTypeId = UUID.randomUUID();
        VehicleTypeNotFoundException exception = new VehicleTypeNotFoundException(vehicleTypeId);
        assertEquals("Vehicle type not found with id: " + vehicleTypeId, exception.getMessage());
    }
}