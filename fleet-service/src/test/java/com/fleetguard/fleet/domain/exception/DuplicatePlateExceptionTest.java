package com.fleetguard.fleet.domain.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DuplicatePlateExceptionTest {

    @Test
    void shouldReturnCorrectMessage() {
        DuplicatePlateException exception = new DuplicatePlateException("ABC123");
        assertEquals("A vehicle with plate 'ABC123' already exists", exception.getMessage());
    }
}