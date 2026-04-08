package com.fleetguard.fleet.domain.model.vehicle;

import com.fleetguard.fleet.domain.exception.InactiveVehicleException;
import com.fleetguard.fleet.domain.exception.InvalidMileageException;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Vehicle")
class VehicleTest {

    private VehicleType vehicleType;
    private Plate plate;
    private Vin vin;

    @BeforeEach
    void setUp() {
        vehicleType = new VehicleType(UUID.randomUUID(), "Pickup", "Pickup truck");
        plate = new Plate("ABC-1234");
        vin = new Vin("1HGCM82633A123456");
    }

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("new vehicle is ACTIVE with zero mileage")
        void newVehicleHasDefaults() {
            Vehicle vehicle = Vehicle.create(plate, "Toyota", "Hilux", 2023, "Diesel", vin, vehicleType);

            assertThat(vehicle.getId()).isNotNull();
            assertThat(vehicle.getStatus()).isEqualTo(VehicleStatus.ACTIVE);
            assertThat(vehicle.getCurrentMileage().getValue()).isZero();
        }

        @Test
        @DisplayName("rejects null brand")
        void rejectsNullBrand() {
            assertThatThrownBy(() -> Vehicle.create(plate, null, "Hilux", 2023, "Diesel", vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La marca no puede ser nula o vacía");
        }

        @Test
        @DisplayName("rejects blank brand")
        void rejectsBlankBrand() {
            assertThatThrownBy(() -> Vehicle.create(plate, "  ", "Hilux", 2023, "Diesel", vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("La marca no puede ser nula o vacía");
        }

        @Test
        @DisplayName("rejects null model")
        void rejectsNullModel() {
            assertThatThrownBy(() -> Vehicle.create(plate, "Toyota", null, 2023, "Diesel", vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El modelo no puede ser nulo o vacío");
        }

        @Test
        @DisplayName("rejects blank model")
        void rejectsBlankModel() {
            assertThatThrownBy(() -> Vehicle.create(plate, "Toyota", "  ", 2023, "Diesel", vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El modelo no puede ser nulo o vacío");
        }

        @Test
        @DisplayName("rejects null fuel type")
        void rejectsNullFuelType() {
            assertThatThrownBy(() -> Vehicle.create(plate, "Toyota", "Hilux", 2023, null, vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El tipo de combustible no puede ser nulo o vacío");
        }

        @Test
        @DisplayName("rejects blank fuel type")
        void rejectsBlankFuelType() {
            assertThatThrownBy(() -> Vehicle.create(plate, "Toyota", "Hilux", 2023, "  ", vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El tipo de combustible no puede ser nulo o vacío");
        }

        @Test
        @DisplayName("rejects year zero — boundary")
        void rejectsYearZero() {
            assertThatThrownBy(() -> Vehicle.create(plate, "Toyota", "Hilux", 0, "Diesel", vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El año debe ser un número entero positivo");
        }

        @Test
        @DisplayName("rejects negative year")
        void rejectsNegativeYear() {
            assertThatThrownBy(() -> Vehicle.create(plate, "Toyota", "Hilux", -1, "Diesel", vin, vehicleType))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El año debe ser un número entero positivo");
        }

        @Test
        @DisplayName("rejects null vehicle type")
        void rejectsNullVehicleType() {
            assertThatThrownBy(() -> Vehicle.create(plate, "Toyota", "Hilux", 2023, "Diesel", vin, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El tipo de vehículo no puede ser nulo");
        }
    }

    @Nested
    @DisplayName("Update mileage")
    class UpdateMileage {

        @Test
        @DisplayName("happy path — accepts mileage higher than current")
        void acceptsHigherMileage() {
            Vehicle vehicle = Vehicle.create(plate, "Toyota", "Hilux", 2023, "Diesel", vin, vehicleType);
            vehicle.updateMileage(new Mileage(1_000L));
            assertThat(vehicle.getCurrentMileage().getValue()).isEqualTo(1_000L);
        }

        @Test
        @DisplayName("boundary — rejects mileage of exactly zero")
        void rejectsZeroMileage() {
            Vehicle vehicle = Vehicle.create(plate, "Toyota", "Hilux", 2023, "Diesel", vin, vehicleType);
            assertThatThrownBy(() -> vehicle.updateMileage(new Mileage(0L)))
                    .isInstanceOf(InvalidMileageException.class)
                    .hasMessage("El valor del kilometraje debe ser mayor que cero");
        }

        @Test
        @DisplayName("error handling — rejects mileage lower than current")
        void rejectsLowerMileage() {
            Vehicle vehicle = Vehicle.create(plate, "Toyota", "Hilux", 2023, "Diesel", vin, vehicleType);
            vehicle.updateMileage(new Mileage(5_000L));

            assertThatThrownBy(() -> vehicle.updateMileage(new Mileage(4_999L)))
                    .isInstanceOf(InvalidMileageException.class);
        }

        @Test
        @DisplayName("error handling — rejects update on INACTIVE vehicle")
        void rejectsInactiveVehicle() {
            Vehicle inactive = new Vehicle(
                    UUID.randomUUID(), plate, "Toyota", "Hilux",
                    2023, "Diesel", vin, VehicleStatus.INACTIVE,
                    new Mileage(0L), vehicleType);

            assertThatThrownBy(() -> inactive.updateMileage(new Mileage(1_000L)))
                    .isInstanceOf(InactiveVehicleException.class);
        }
    }
}