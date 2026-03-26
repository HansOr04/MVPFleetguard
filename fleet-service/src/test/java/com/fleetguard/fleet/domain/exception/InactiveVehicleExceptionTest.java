package com.fleetguard.fleet.domain.exception;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InactiveVehicleExceptionTest {

    @Test
    void shouldReturnCorrectMessage() {
        UUID vehicleId = UUID.randomUUID();
        InactiveVehicleException exception = new InactiveVehicleException(vehicleId);
        assertEquals("Vehicle " + vehicleId + " is not active", exception.getMessage());
    }
}