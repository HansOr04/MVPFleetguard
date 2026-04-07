package com.fleetguard.fleet.domain.factory;

import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MileageLogFactory")
class MileageLogFactoryTest {

    private MileageLogFactory factory;
    private Vehicle vehicle;
    private final LocalDateTime recordedAt = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        factory = new MileageLogFactory();
        vehicle = Vehicle.create(
                new Plate("ABC-1234"),
                "Toyota", "Hilux", 2023, "Diesel",
                new Vin("1HGCM82633A123456"),
                new VehicleType(UUID.randomUUID(), "Pickup", "Utility truck")
        );
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("maps vehicle data and mileage values correctly")
        void mapsCorrectly() {
            MileageLog log = factory.create(vehicle, 1000L, "driver@fleetguard.com", recordedAt);

            assertThat(log.getVehicleId()).isEqualTo(vehicle.getId());
            assertThat(log.getVehicleTypeId()).isEqualTo(vehicle.getVehicleType().getId());
            assertThat(log.getVehicleStatus()).isEqualTo("ACTIVE");
            assertThat(log.getPreviousMileage().getValue()).isZero();
            assertThat(log.getMileageValue().getValue()).isEqualTo(1000L);
            assertThat(log.getKmTraveled()).isEqualTo(1000L);
            assertThat(log.getRecordedBy()).isEqualTo("driver@fleetguard.com");
            assertThat(log.getRecordedAt()).isEqualTo(recordedAt);
        }

        @Test
        @DisplayName("excessiveIncrement is false when increment is within 2000 km — boundary")
        void notExcessiveAt2000() {
            vehicle.updateMileage(new Mileage(10_000L));

            MileageLog log = factory.create(vehicle, 12_000L, "driver@fleetguard.com", recordedAt);

            assertThat(log.isExcessiveIncrement()).isFalse();
        }

        @Test
        @DisplayName("excessiveIncrement is true when increment exceeds 2000 km — boundary")
        void excessiveAt2001() {
            vehicle.updateMileage(new Mileage(10_000L));

            MileageLog log = factory.create(vehicle, 12_001L, "driver@fleetguard.com", recordedAt);

            assertThat(log.isExcessiveIncrement()).isTrue();
        }

        @Test
        @DisplayName("registers domain event after creation")
        void registersDomainEvent() {
            MileageLog log = factory.create(vehicle, 1000L, "driver@fleetguard.com", recordedAt);

            assertThat(log.pullDomainEvents()).hasSize(1);
        }

        @Test
        @DisplayName("previous mileage is taken from vehicle current mileage before update")
        void capturesPreviousMileage() {
            vehicle.updateMileage(new Mileage(5_000L));

            MileageLog log = factory.create(vehicle, 6_000L, "driver@fleetguard.com", recordedAt);

            assertThat(log.getPreviousMileage().getValue()).isEqualTo(5_000L);
            assertThat(log.getMileageValue().getValue()).isEqualTo(6_000L);
            assertThat(log.getKmTraveled()).isEqualTo(1_000L);
        }
    }
}