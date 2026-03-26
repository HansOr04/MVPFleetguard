package com.fleetguard.fleet.infrastructure.persistence.mapper;

import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleStatus;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleTypeJpaEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VehicleMapperTest {

    @Test
    void shouldMapDomainToJpaEntity() {
        UUID vehicleTypeId = UUID.randomUUID();
        VehicleType vehicleType = new VehicleType(vehicleTypeId, "Sedan", "A compact car");
        Vehicle vehicle = new Vehicle(
                UUID.randomUUID(),
                new Plate("ABC123"),
                "Toyota",
                "Corolla",
                2023,
                "Gasoline",
                new Vin("1HGCM82633A123456"),
                VehicleStatus.ACTIVE,
                new Mileage(1000),
                vehicleType
        );

        VehicleJpaEntity entity = VehicleMapper.toJpaEntity(vehicle);

        assertEquals(vehicle.getId(), entity.getId());
        assertEquals(vehicle.getPlate().getValue(), entity.getPlate());
        assertEquals(vehicle.getBrand(), entity.getBrand());
        assertEquals(vehicle.getModel(), entity.getModel());
        assertEquals(vehicle.getYear(), entity.getYear());
        assertEquals(vehicle.getFuelType(), entity.getFuelType());
        assertEquals(vehicle.getVin().getValue(), entity.getVin());
        assertEquals(vehicle.getStatus().name(), entity.getStatus());
        assertEquals(vehicle.getCurrentMileage().getValue(), entity.getCurrentMileage());
        assertEquals(vehicle.getVehicleType().getId(), entity.getVehicleType().getId());
    }

    @Test
    void shouldMapJpaEntityToDomain() {
        UUID vehicleId = UUID.randomUUID();
        UUID vehicleTypeId = UUID.randomUUID();
        VehicleTypeJpaEntity vehicleTypeJpaEntity = new VehicleTypeJpaEntity(vehicleTypeId, "SUV", "A family car");
        VehicleJpaEntity entity = new VehicleJpaEntity(
                vehicleId,
                "DEF456",
                "Honda",
                "Civic",
                2022,
                "Diesel",
                "2HGFB2F50EH000000",
                "ACTIVE",
                20000,
                vehicleTypeJpaEntity
        );

        Vehicle vehicle = VehicleMapper.toDomain(entity);

        assertEquals(entity.getId(), vehicle.getId());
        assertEquals(entity.getPlate(), vehicle.getPlate().getValue());
        assertEquals(entity.getBrand(), vehicle.getBrand());
        assertEquals(entity.getModel(), vehicle.getModel());
        assertEquals(entity.getYear(), vehicle.getYear());
        assertEquals(entity.getFuelType(), vehicle.getFuelType());
        assertEquals(entity.getVin(), vehicle.getVin().getValue());
        assertEquals(VehicleStatus.ACTIVE, vehicle.getStatus());
        assertEquals(entity.getCurrentMileage(), vehicle.getCurrentMileage().getValue());
        assertEquals(entity.getVehicleType().getId(), vehicle.getVehicleType().getId());
    }
}