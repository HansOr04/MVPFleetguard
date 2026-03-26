package com.fleetguard.fleet.domain.model.vehicle;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTypeTest {

    @Test
    void shouldCreateValidVehicleType() {
        UUID id = UUID.randomUUID();
        String name = "Sedán";
        String description = "Automóvil de uso urbano";

        VehicleType vehicleType = new VehicleType(id, name, description);

        assertNotNull(vehicleType.getId());
        assertEquals(id, vehicleType.getId());
        assertEquals(name, vehicleType.getName());
        assertEquals(description, vehicleType.getDescription());
    }

    @Test
    void shouldSupportEquality() {
        UUID id = UUID.randomUUID();
        VehicleType vehicleType1 = new VehicleType(id, "SUV", "Vehículo utilitario deportivo");
        VehicleType vehicleType2 = new VehicleType(id, "SUV", "Vehículo utilitario deportivo");

        assertEquals(vehicleType1, vehicleType2);
        assertEquals(vehicleType1.hashCode(), vehicleType2.hashCode());
    }

    @Test
    void shouldNotBeEqualForDifferentVehicleTypes() {
        VehicleType vehicleType1 = new VehicleType(UUID.randomUUID(), "SUV", "Vehículo utilitario deportivo");
        VehicleType vehicleType2 = new VehicleType(UUID.randomUUID(), "Sedán", "Automóvil compacto urbano");

        assertNotEquals(vehicleType1, vehicleType2);
    }
}