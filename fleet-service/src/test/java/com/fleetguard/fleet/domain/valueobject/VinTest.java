package com.fleetguard.fleet.domain.valueobject;

import com.fleetguard.fleet.domain.exception.InvalidVinException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VinTest {

    @Test
    void shouldCreateValidVin() {
        String validVin = "1HGCM82633A123456";

        Vin vin = new Vin(validVin);

        assertNotNull(vin);
        assertEquals(validVin, vin.getValue());
    }

    @Test
    void shouldThrowExceptionForNullVin() {
        InvalidVinException exception = assertThrows(InvalidVinException.class, () -> new Vin(null));

        assertEquals("VIN must be exactly 17 characters, got: null", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidVinLength() {
        InvalidVinException exception = assertThrows(InvalidVinException.class, () -> new Vin("SHORT"));

        assertEquals("VIN must be exactly 17 characters, got: 5", exception.getMessage());
    }

    @Test
    void shouldBeEqualForSameValues() {
        Vin vin1 = new Vin("1HGCM82633A123456");
        Vin vin2 = new Vin("1HGCM82633A123456");

        assertEquals(vin1, vin2);
        assertEquals(vin1.hashCode(), vin2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        Vin vin1 = new Vin("1HGCM82633A123456");
        Vin vin2 = new Vin("2HGCM82633A654321");

        assertNotEquals(vin1, vin2);
    }
}