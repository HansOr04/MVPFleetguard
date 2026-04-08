package com.fleetguard.fleet.domain.valueobject;

import com.fleetguard.fleet.domain.exception.InvalidMileageException;

public class Mileage {

    private final long value;

    public Mileage(long value) {
        if (value < 0) {
            throw new InvalidMileageException("El kilometraje no puede ser negativo");
        }
        this.value = value;
    }

    public static Mileage zero() {
        return new Mileage(0);
    }

    public void assertNewMileageIsNotLower(Mileage newMileage) {
        if (newMileage.value < this.value) {
            throw new InvalidMileageException(
                    "El nuevo kilometraje " + newMileage.value +
                            " no puede ser menor que el actual " + this.value);
        }
    }

    public boolean isExcessiveIncrement(Mileage previous) {
        return (this.value - previous.value) > 2000;
    }

    public long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mileage mileage = (Mileage) o;
        return value == mileage.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
}