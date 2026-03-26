package com.fleetguard.fleet.domain.model.vehicle;

import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void shouldCreateVehicleWithDefaults() {
        Plate plate = new Plate("ABC123");
        Vin vin = new Vin("1HGCM82633A123456");
        VehicleType vehicleType = new VehicleType(UUID.randomUUID(), "Sedán", "Autómovil compacto ideal para ciudad");

        Vehicle vehicle = Vehicle.create(plate, "Toyota", "Corolla", 2023, "Gasoline", vin, vehicleType);

        assertNotNull(vehicle.getId());
        assertEquals(plate, vehicle.getPlate());
        assertEquals("Toyota", vehicle.getBrand());
        assertEquals("Corolla", vehicle.getModel());
        assertEquals(2023, vehicle.getYear());
        assertEquals("Gasoline", vehicle.getFuelType());
        assertEquals(vin, vehicle.getVin());
        assertEquals(VehicleStatus.ACTIVE, vehicle.getStatus());
        assertEquals(0, vehicle.getCurrentMileage().getValue());
        assertEquals(vehicleType, vehicle.getVehicleType());
    }

    @Test
    void shouldThrowExceptionForNullBrand() {
        Plate plate = new Plate("ABC123");
        Vin vin = new Vin("1HGCM82633A123456");
        VehicleType vehicleType = new VehicleType(UUID.randomUUID(), "Sedán", "Autómovil compacto ideal para ciudad");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vehicle.create(plate, null, "Corolla", 2023, "Gasoline", vin, vehicleType));
        assertEquals("Brand cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullModel() {
        Plate plate = new Plate("ABC123");
        Vin vin = new Vin("1HGCM82633A123456");
        VehicleType vehicleType = new VehicleType(UUID.randomUUID(), "Sedán", "Autómovil compacto ideal para ciudad");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vehicle.create(plate, "Toyota", null, 2023, "Gasoline", vin, vehicleType));
        assertEquals("Model cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNegativeYear() {
        Plate plate = new Plate("ABC123");
        Vin vin = new Vin("1HGCM82633A123456");
        VehicleType vehicleType = new VehicleType(UUID.randomUUID(), "Sedán", "Autómovil compacto ideal para ciudad");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Vehicle.create(plate, "Toyota", "Corolla", -2023, "Gasoline", vin, vehicleType));
        assertEquals("Year must be a positive integer", exception.getMessage());
    }

    @Test
    void shouldUpdateMileageIfVehicleIsActive() {
        Plate plate = new Plate("ABC123");
        Vin vin = new Vin("1HGCM82633A123456");
        VehicleType vehicleType = new VehicleType(UUID.randomUUID(), "Sedán", "Autómovil compacto ideal para ciudad");
        Vehicle vehicle = Vehicle.create(plate, "Toyota", "Corolla", 2023, "Gasoline", vin, vehicleType);

        Mileage newMileage = new Mileage(1500);
        vehicle.updateMileage(newMileage);

        assertEquals(newMileage, vehicle.getCurrentMileage());
    }
}