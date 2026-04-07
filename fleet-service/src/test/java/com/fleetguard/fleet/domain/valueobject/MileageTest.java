package com.fleetguard.fleet.domain.valueobject;

import com.fleetguard.fleet.domain.exception.InvalidMileageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Mileage")
class MileageTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("zero is valid — lower boundary")
        void zeroIsValid() {
            assertThat(Mileage.zero().getValue()).isZero();
        }

        @Test
        @DisplayName("positive value is valid")
        void positiveIsValid() {
            assertThat(new Mileage(1L).getValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("negative value is rejected — below lower boundary")
        void negativeIsRejected() {
            assertThatThrownBy(() -> new Mileage(-1L))
                    .isInstanceOf(InvalidMileageException.class)
                    .hasMessage("Mileage cannot be negative");
        }
    }

    @Nested
    @DisplayName("Excessive increment detection")
    class ExcessiveIncrement {

        @Test
        @DisplayName("increment of exactly 2000 km is NOT excessive — upper valid boundary")
        void exactly2000IsNotExcessive() {
            Mileage previous = new Mileage(10_000L);
            Mileage current = new Mileage(12_000L);
            assertThat(current.isExcessiveIncrement(previous)).isFalse();
        }

        @Test
        @DisplayName("increment of 2001 km IS excessive — lower excessive boundary")
        void exactly2001IsExcessive() {
            Mileage previous = new Mileage(10_000L);
            Mileage current = new Mileage(12_001L);
            assertThat(current.isExcessiveIncrement(previous)).isTrue();
        }
    }

    @Nested
    @DisplayName("assertNewMileageIsNotLower")
    class AssertNewMileageIsNotLower {

        @Test
        @DisplayName("equal mileage is accepted — boundary")
        void equalIsAccepted() {
            Mileage current = new Mileage(5_000L);
            assertThatNoException().isThrownBy(() -> current.assertNewMileageIsNotLower(new Mileage(5_000L)));
        }

        @Test
        @DisplayName("higher mileage is accepted")
        void higherIsAccepted() {
            Mileage current = new Mileage(5_000L);
            assertThatNoException().isThrownBy(() -> current.assertNewMileageIsNotLower(new Mileage(5_001L)));
        }

        @Test
        @DisplayName("lower mileage is rejected")
        void lowerIsRejected() {
            Mileage current = new Mileage(5_000L);
            assertThatThrownBy(() -> current.assertNewMileageIsNotLower(new Mileage(4_999L)))
                    .isInstanceOf(InvalidMileageException.class)
                    .hasMessage("New mileage 4999 cannot be less than current 5000");
        }
    }

    @Nested
    @DisplayName("Equality and hashCode")
    class EqualityAndHashCode {

        @Test
        @DisplayName("same instance is equal to itself")
        void sameInstanceIsEqual() {
            Mileage mileage = new Mileage(1000L);
            assertThat(mileage).isEqualTo(mileage);
        }

        @Test
        @DisplayName("two mileages with same value are equal")
        void equalByValue() {
            assertThat(new Mileage(1000L)).isEqualTo(new Mileage(1000L));
        }

        @Test
        @DisplayName("null is not equal")
        void nullIsNotEqual() {
            assertThat(new Mileage(1000L)).isNotEqualTo(null);
        }

        @Test
        @DisplayName("different type is not equal")
        void differentTypeIsNotEqual() {
            assertThat(new Mileage(1000L)).isNotEqualTo(1000L);
        }

        @Test
        @DisplayName("same value produces same hashCode")
        void sameHashCode() {
            assertThat(new Mileage(1000L).hashCode())
                    .isEqualTo(new Mileage(1000L).hashCode());
        }
    }
}