package com.fleetguard.fleet.domain.valueobject;

import com.fleetguard.fleet.domain.exception.InvalidMileageException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MileageTest {

    @Test
    void shouldCreateValidMileage() {
        long validMileage = 1000;

        Mileage mileage = new Mileage(validMileage);

        assertNotNull(mileage);
        assertEquals(validMileage, mileage.getValue());
    }

    @Test
    void shouldThrowExceptionForNegativeMileage() {
        InvalidMileageException exception =
                assertThrows(InvalidMileageException.class, () -> new Mileage(-100));

        assertEquals("Mileage cannot be negative", exception.getMessage());
    }

    @Test
    void shouldCreateMileageWithZeroValue() {
        Mileage mileage = Mileage.zero();

        assertNotNull(mileage);
        assertEquals(0, mileage.getValue());
    }

    @Test
    void shouldValidateThatNewMileageIsNotLessThanCurrentMileage() {
        Mileage currentMileage = new Mileage(1000);
        Mileage newMileage = new Mileage(500);

        InvalidMileageException exception =
                assertThrows(InvalidMileageException.class,
                        () -> currentMileage.validateNotLessThan(newMileage));

        assertEquals("New mileage 500 cannot be less than current 1000", exception.getMessage());
    }

    @Test
    void shouldAllowNewMileageEqualOrGreater() {
        Mileage currentMileage = new Mileage(1000);
        Mileage newMileage = new Mileage(1000);
        Mileage greaterMileage = new Mileage(1500);

        assertDoesNotThrow(() -> currentMileage.validateNotLessThan(newMileage));
        assertDoesNotThrow(() -> currentMileage.validateNotLessThan(greaterMileage));
    }

    @Test
    void shouldDetectExcessiveIncrement() {
        Mileage previousMileage = new Mileage(1000);
        Mileage excessiveIncrementMileage = new Mileage(3100);
        Mileage normalIncrementMileage = new Mileage(2000);

        assertTrue(excessiveIncrementMileage.isExcessiveIncrement(previousMileage));
        assertFalse(normalIncrementMileage.isExcessiveIncrement(previousMileage));
    }

    @Test
    void shouldReturnTrueForEqualValues() {
        Mileage mileage1 = new Mileage(1000);
        Mileage mileage2 = new Mileage(1000);

        assertEquals(mileage1, mileage2);
        assertEquals(mileage1.hashCode(), mileage2.hashCode());
    }

    @Test
    void shouldReturnFalseForDifferentValues() {
        Mileage mileage1 = new Mileage(1000);
        Mileage mileage2 = new Mileage(2000);

        assertNotEquals(mileage1, mileage2);
    }
}