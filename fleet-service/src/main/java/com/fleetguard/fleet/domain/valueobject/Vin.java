package com.fleetguard.fleet.domain.valueobject;

import com.fleetguard.fleet.domain.exception.InvalidVinException;

public class Vin {

    private final String value;

    public Vin(String value) {
        if (value == null || value.length() != 17) {
            throw new InvalidVinException(
                    "El VIN debe tener exactamente 17 caracteres, se obtuvo: " +
                            (value == null ? "null" : value.length()));
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
        Vin vin = (Vin) o;
        return value.equals(vin.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
