package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.exception.DuplicatePlateException;
import com.fleetguard.fleet.domain.exception.VehicleTypeNotFoundException;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterVehicleServiceTest {

    @Mock
    private VehicleRepositoryPort vehicleRepository;

    @InjectMocks
    private RegisterVehicleService registerVehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterVehicle() {
        RegisterVehicleCommand command = new RegisterVehicleCommand(
                "ABC123", "Toyota", "Corolla", 2023, "Gasoline",
                "1HGCM82633A123456", UUID.randomUUID()
        );

        Plate plate = new Plate(command.plate());
        Vin vin = new Vin(command.vin());
        VehicleType vehicleType = new VehicleType(command.vehicleTypeId(), "Sedan", "Compact car");
        Vehicle vehicle = Vehicle.create(plate, command.brand(), command.model(),
                command.year(), command.fuelType(), vin, vehicleType);

        when(vehicleRepository.existsByPlate(command.plate())).thenReturn(false);
        when(vehicleRepository.findVehicleTypeById(command.vehicleTypeId())).thenReturn(Optional.of(vehicleType));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        var response = registerVehicleService.execute(command);

        assertNotNull(response);
        assertEquals(command.plate(), response.plate());
        assertEquals(command.brand(), response.brand());
        assertEquals(command.model(), response.model());
        verify(vehicleRepository, times(1)).existsByPlate(command.plate());
        verify(vehicleRepository, times(1)).findVehicleTypeById(command.vehicleTypeId());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowExceptionWhenPlateAlreadyExists() {
        RegisterVehicleCommand command = new RegisterVehicleCommand(
                "ABC123", "Toyota", "Corolla", 2023, "Gasoline",
                "1HGCM82633A123456", UUID.randomUUID()
        );

        when(vehicleRepository.existsByPlate(command.plate())).thenReturn(true);

        assertThrows(DuplicatePlateException.class, () -> registerVehicleService.execute(command));
        verify(vehicleRepository, times(1)).existsByPlate(command.plate());
        verify(vehicleRepository, never()).findVehicleTypeById(any(UUID.class));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    void shouldThrowExceptionWhenVehicleTypeNotFound() {
        RegisterVehicleCommand command = new RegisterVehicleCommand(
                "ABC123", "Toyota", "Corolla", 2023, "Gasoline",
                "1HGCM82633A123456", UUID.randomUUID()
        );

        when(vehicleRepository.existsByPlate(command.plate())).thenReturn(false);
        when(vehicleRepository.findVehicleTypeById(command.vehicleTypeId())).thenReturn(Optional.empty());

        assertThrows(VehicleTypeNotFoundException.class, () -> registerVehicleService.execute(command));
        verify(vehicleRepository, times(1)).existsByPlate(command.plate());
        verify(vehicleRepository, times(1)).findVehicleTypeById(command.vehicleTypeId());
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
}