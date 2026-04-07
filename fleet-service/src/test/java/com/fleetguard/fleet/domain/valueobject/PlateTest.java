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
                    .hasMessage("Plate cannot be null or empty");
        }

        @Test
        @DisplayName("accepts valid plate")
        void acceptsValidPlate() {
            assertThat(new Plate("ABC-1234").getValue()).isEqualTo("ABC-1234");
        }
    }
}