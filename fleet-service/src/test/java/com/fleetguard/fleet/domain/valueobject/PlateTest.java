package com.fleetguard.fleet.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlateTest {

    @Test
    void shouldCreateValidPlate() {
        String validPlate = "ABC123";

        Plate plate = new Plate(validPlate);

        assertNotNull(plate);
        assertEquals(validPlate, plate.getValue());
    }

    @Test
    void shouldThrowExceptionForNullPlate() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new Plate(null));

        assertEquals("Plate cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForEmptyPlate() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new Plate(""));

        assertEquals("Plate cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForBlankPlate() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> new Plate("   "));

        assertEquals("Plate cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldReturnTrueForEqualPlates() {
        Plate plate1 = new Plate("ABC123");
        Plate plate2 = new Plate("ABC123");

        assertEquals(plate1, plate2);
        assertEquals(plate1.hashCode(), plate2.hashCode());
    }

    @Test
    void shouldReturnFalseForDifferentPlates() {
        Plate plate1 = new Plate("ABC123");
        Plate plate2 = new Plate("XYZ789");

        assertNotEquals(plate1, plate2);
    }
}