package com.fleetguard.fleet.domain.valueobject;

import com.fleetguard.fleet.domain.exception.InvalidVinException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Vin")
class VinTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("accepts exactly 17 characters — valid boundary")
        void accepts17Chars() {
            assertThat(new Vin("1HGCM82633A123456").getValue()).isEqualTo("1HGCM82633A123456");
        }

        @Test
        @DisplayName("rejects 16 characters — below boundary")
        void rejects16Chars() {
            assertThatThrownBy(() -> new Vin("1HGCM82633A12345"))
                    .isInstanceOf(InvalidVinException.class)
                    .hasMessageContaining("se obtuvo: 16");
        }

        @Test
        @DisplayName("rejects 18 characters — above boundary")
        void rejects18Chars() {
            assertThatThrownBy(() -> new Vin("1HGCM82633A123456X"))
                    .isInstanceOf(InvalidVinException.class)
                    .hasMessageContaining("se obtuvo: 18");
        }

        @Test
        @DisplayName("rejects null")
        void rejectsNull() {
            assertThatThrownBy(() -> new Vin(null))
                    .isInstanceOf(InvalidVinException.class)
                    .hasMessageContaining("se obtuvo: null");
        }
    }

    @Nested
    @DisplayName("Equality and hashCode")
    class EqualityAndHashCode {

        @Test
        @DisplayName("same instance is equal to itself")
        void sameInstanceIsEqual() {
            Vin vin = new Vin("1HGCM82633A123456");
            assertThat(vin).isEqualTo(vin);
        }

        @Test
        @DisplayName("two VINs with same value are equal")
        void equalByValue() {
            assertThat(new Vin("1HGCM82633A123456"))
                    .isEqualTo(new Vin("1HGCM82633A123456"));
        }

        @Test
        @DisplayName("null is not equal")
        void nullIsNotEqual() {
            assertThat(new Vin("1HGCM82633A123456")).isNotEqualTo(null);
        }

        @Test
        @DisplayName("different type is not equal")
        void differentTypeIsNotEqual() {
            assertThat(new Vin("1HGCM82633A123456")).isNotEqualTo("1HGCM82633A123456");
        }

        @Test
        @DisplayName("same value produces same hashCode")
        void sameHashCode() {
            assertThat(new Vin("1HGCM82633A123456").hashCode())
                    .isEqualTo(new Vin("1HGCM82633A123456").hashCode());
        }
    }
}