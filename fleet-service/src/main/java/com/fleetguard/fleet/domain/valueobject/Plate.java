package com.fleetguard.fleet.domain.valueobject;

public class Plate {

    private final String value;

    public Plate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Plate cannot be null or empty");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plate plate = (Plate) o;
        return value.equals(plate.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
