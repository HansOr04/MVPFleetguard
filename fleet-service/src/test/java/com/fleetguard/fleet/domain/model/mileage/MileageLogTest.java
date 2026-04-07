package com.fleetguard.fleet.domain.model.mileage;

import com.fleetguard.fleet.domain.event.MileageRegistered;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MileageLog domain")
class MileageLogTest {

    private MileageLog buildLog(long previous, long current, String recordedBy) {
        return MileageLog.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "ACTIVE",
                new Mileage(previous),
                new Mileage(current),
                LocalDateTime.now(),
                recordedBy,
                false
        );
    }

    @Nested
    @DisplayName("Domain events")
    class DomainEvents {

        @Test
        @DisplayName("create — generates exactly one MileageRegistered event")
        void generatesMileageRegisteredEvent() {
            MileageLog log = buildLog(0, 1000, "driver@fleetguard.com");

            List<?> events = log.pullDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(MileageRegistered.class);
        }

        @Test
        @DisplayName("pullDomainEvents — clears events after first pull")
        void clearsEventsAfterPull() {
            MileageLog log = buildLog(0, 1000, "driver@fleetguard.com");

            log.pullDomainEvents();
            List<?> secondPull = log.pullDomainEvents();

            assertThat(secondPull).isEmpty();
        }

        @Test
        @DisplayName("MileageRegistered event — contains correct vehicleId")
        void eventContainsCorrectVehicleId() {
            UUID vehicleId = UUID.randomUUID();
            MileageLog log = MileageLog.create(
                    vehicleId, UUID.randomUUID(), "ACTIVE",
                    new Mileage(0), new Mileage(1000),
                    LocalDateTime.now(), "driver@fleetguard.com", false);

            MileageRegistered event = (MileageRegistered) log.pullDomainEvents().get(0);

            assertThat(event.getVehicleId()).isEqualTo(vehicleId);
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("create — throws when recordedBy is null")
        void throwsWhenRecordedByIsNull() {
            assertThatThrownBy(() -> buildLog(0, 1000, null))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("create — throws when recordedBy is blank")
        void throwsWhenRecordedByIsBlank() {
            assertThatThrownBy(() -> buildLog(0, 1000, "  "))
                    .isInstanceOf(Exception.class);
        }
    }
}