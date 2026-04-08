package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.exception.DuplicatePlateException;
import com.fleetguard.fleet.domain.exception.DuplicateVinException;
import com.fleetguard.fleet.domain.exception.VehicleTypeNotFoundException;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterVehicleService")
class RegisterVehicleServiceTest {

    @Mock
    private VehicleRepositoryPort vehicleRepository;

    @InjectMocks
    private RegisterVehicleService service;

    private final UUID typeId = UUID.randomUUID();
    private final VehicleType vehicleType = new VehicleType(typeId, "Pickup", "Pickup truck");

    private RegisterVehicleCommand validCommand() {
        return new RegisterVehicleCommand(
                "ABC-1234", "Toyota", "Hilux", 2023,
                "Diesel", "1HGCM82633A123456", typeId);
    }

    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("registers vehicle and returns correct response")
        void registersVehicle() {
            when(vehicleRepository.existsByPlate("ABC-1234")).thenReturn(false);
            when(vehicleRepository.findVehicleTypeById(typeId)).thenReturn(Optional.of(vehicleType));
            when(vehicleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            RegisterVehicleResponse response = service.execute(validCommand());

            assertThat(response.plate()).isEqualTo("ABC-1234");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.currentMileage()).isZero();
            verify(vehicleRepository).save(any());
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("rejects duplicate plate — never calls save")
        void rejectsDuplicatePlate() {
            when(vehicleRepository.existsByPlate("ABC-1234")).thenReturn(true);

            assertThatThrownBy(() -> service.execute(validCommand()))
                    .isInstanceOf(DuplicatePlateException.class);

            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects duplicate vin — never calls save")
        void rejectsDuplicateVin() {
            when(vehicleRepository.existsByVin("1HGCM82633A123456")).thenReturn(true);

            assertThatThrownBy(() -> service.execute(validCommand()))
                    .isInstanceOf(DuplicateVinException.class);

            verify(vehicleRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects unknown vehicle type — never calls save")
        void rejectsUnknownVehicleType() {
            when(vehicleRepository.existsByPlate("ABC-1234")).thenReturn(false);
            when(vehicleRepository.findVehicleTypeById(typeId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.execute(validCommand()))
                    .isInstanceOf(VehicleTypeNotFoundException.class);

            verify(vehicleRepository, never()).save(any());
        }
    }
}