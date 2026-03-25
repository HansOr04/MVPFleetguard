package com.fleetguard.fleet.domain.valueobject;

import com.fleetguard.fleet.domain.exception.InvalidMileageException;

public class Mileage {

    private final long value;

    public Mileage(long value) {
        if (value < 0) {
            throw new InvalidMileageException("Mileage cannot be negative");
        }
        this.value = value;
    }

    public static Mileage zero() {
        return new Mileage(0);
    }

    public void validateNotLessThan(Mileage newMileage) {
        if (newMileage.value < this.value) {
            throw new InvalidMileageException(
                    "New mileage " + newMileage.value +
                            " cannot be less than current " + this.value);
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
