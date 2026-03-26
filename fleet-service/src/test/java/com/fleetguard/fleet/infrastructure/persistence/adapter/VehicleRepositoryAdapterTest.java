package com.fleetguard.fleet.infrastructure.persistence.adapter;

import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleStatus;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleTypeJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.mapper.VehicleMapper;
import com.fleetguard.fleet.infrastructure.persistence.repository.VehicleJpaRepository;
import com.fleetguard.fleet.infrastructure.persistence.repository.VehicleTypeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleRepositoryAdapterTest {

    @Mock
    private VehicleJpaRepository vehicleRepository;

    @Mock
    private VehicleTypeJpaRepository vehicleTypeRepository;

    @InjectMocks
    private VehicleRepositoryAdapter vehicleRepositoryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldSaveVehicle() {
        Vehicle vehicle = new Vehicle(
                UUID.randomUUID(),
                new Plate("XYZ789"),
                "Ford",
                "Focus",
                2021,
                "Gasoline",
                new Vin("3FAHP0HA7AR123456"),
                VehicleStatus.ACTIVE,
                new Mileage(3000),
                new VehicleType(UUID.randomUUID(), "Hatchback", "Small car")
        );
        VehicleJpaEntity entity = VehicleMapper.toJpaEntity(vehicle);
        when(vehicleRepository.save(any(VehicleJpaEntity.class))).thenReturn(entity);

        Vehicle result = vehicleRepositoryAdapter.save(vehicle);

        assertNotNull(result);
        assertEquals(vehicle.getId(), result.getId());
        verify(vehicleRepository, times(1)).save(any(VehicleJpaEntity.class));
    }

    @Test
    void shouldFindVehicleByPlate() {
        String plate = "DEF456";
        VehicleJpaEntity entity = new VehicleJpaEntity(
                UUID.randomUUID(),
                plate,
                "Chevrolet",
                "Malibu",
                2020,
                "Gasoline",
                "1G1ZE5ST5KF123456",
                "ACTIVE",
                15000,
                new VehicleTypeJpaEntity(UUID.randomUUID(), "Sedan", "Mid-size car")
        );
        when(vehicleRepository.findByPlate(plate)).thenReturn(Optional.of(entity));

        Optional<Vehicle> result = vehicleRepositoryAdapter.findByPlate(plate);

        assertTrue(result.isPresent());
        assertEquals(entity.getPlate(), result.get().getPlate().getValue());
        verify(vehicleRepository, times(1)).findByPlate(plate);
    }

    @Test
    void shouldCheckIfPlateExists() {
        String plate = "XYZ987";
        when(vehicleRepository.existsByPlate(plate)).thenReturn(true);

        boolean exists = vehicleRepositoryAdapter.existsByPlate(plate);

        assertTrue(exists);
        verify(vehicleRepository, times(1)).existsByPlate(plate);
    }

    @Test
    void shouldFindVehicleTypeById() {
        UUID id = UUID.randomUUID();
        VehicleTypeJpaEntity entity = new VehicleTypeJpaEntity(id, "Pickup", "A utility truck");
        when(vehicleTypeRepository.findById(id)).thenReturn(Optional.of(entity));

        Optional<VehicleType> result = vehicleRepositoryAdapter.findVehicleTypeById(id);

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        verify(vehicleTypeRepository, times(1)).findById(id);
    }
}