package com.fleetguard.fleet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidVinExceptionTest {

    @Test
    void shouldReturnCorrectMessage() {
        InvalidVinException exception = new InvalidVinException("Invalid VIN number");
        assertEquals("Invalid VIN number", exception.getMessage());
    }
}