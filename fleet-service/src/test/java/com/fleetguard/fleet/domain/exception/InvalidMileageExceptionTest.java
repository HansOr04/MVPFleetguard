package com.fleetguard.fleet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidMileageExceptionTest {

    @Test
    void shouldReturnCorrectMessage() {
        InvalidMileageException exception = new InvalidMileageException("Invalid mileage value");
        assertEquals("Invalid mileage value", exception.getMessage());
    }
}