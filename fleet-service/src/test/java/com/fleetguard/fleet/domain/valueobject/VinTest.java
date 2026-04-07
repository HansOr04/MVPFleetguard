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
                    .hasMessageContaining("got: 16");
        }

        @Test
        @DisplayName("rejects null")
        void rejectsNull() {
            assertThatThrownBy(() -> new Vin(null))
                    .isInstanceOf(InvalidVinException.class)
                    .hasMessageContaining("got: null");
        }
    }
}