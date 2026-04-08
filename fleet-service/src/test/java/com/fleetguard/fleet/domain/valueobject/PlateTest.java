package com.fleetguard.fleet.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Plate")
class PlateTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @ParameterizedTest(name = "rejects [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("rejects null, empty and blank values")
        void rejectsInvalidValues(String value) {
            assertThatThrownBy(() -> new Plate(value))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La placa no puede ser nula o vacía");
        }

        @Test
        @DisplayName("accepts valid plate")
        void acceptsValidPlate() {
            assertThat(new Plate("ABC-1234").getValue()).isEqualTo("ABC-1234");
        }

        @Test
        @DisplayName("normalizes to uppercase")
        void normalizesToUppercase() {
            assertThat(new Plate("abc-1234").getValue()).isEqualTo("ABC-1234");
        }
    }

    @Nested
    @DisplayName("Equality and hashCode")
    class EqualityAndHashCode {

        @Test
        @DisplayName("same instance is equal to itself")
        void sameInstanceIsEqual() {
            Plate plate = new Plate("ABC-1234");
            assertThat(plate).isEqualTo(plate);
        }

        @Test
        @DisplayName("two plates with same value are equal")
        void equalByValue() {
            assertThat(new Plate("ABC-1234")).isEqualTo(new Plate("ABC-1234"));
        }

        @Test
        @DisplayName("null is not equal")
        void nullIsNotEqual() {
            assertThat(new Plate("ABC-1234")).isNotEqualTo(null);
        }

        @Test
        @DisplayName("different type is not equal")
        void differentTypeIsNotEqual() {
            assertThat(new Plate("ABC-1234")).isNotEqualTo("ABC-1234");
        }

        @Test
        @DisplayName("same value produces same hashCode")
        void sameHashCode() {
            assertThat(new Plate("ABC-1234").hashCode())
                    .isEqualTo(new Plate("ABC-1234").hashCode());
        }
    }
}