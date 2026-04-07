package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageCommand;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageResponse;
import com.fleetguard.fleet.application.ports.out.MileageLogRepositoryPort;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.exception.InvalidMileageException;
import com.fleetguard.fleet.domain.exception.VehicleNotFoundException;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterMileageService")
class RegisterMileageServiceTest {

    @Mock private VehicleRepositoryPort vehicleRepository;
    @Mock private MileageLogRepositoryPort mileageLogRepository;
    @Mock private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private RegisterMileageService service;

    private Vehicle activeVehicle;

    @BeforeEach
    void setUp() {
        VehicleType type = new VehicleType(UUID.randomUUID(), "Pickup", "Pickup truck");
        activeVehicle = Vehicle.create(
                new Plate("ABC-1234"), "Toyota", "Hilux",
                2023, "Diesel", new Vin("1HGCM82633A123456"), type);
    }

    private RegisterMileageCommand commandWith(long mileage) {
        return new RegisterMileageCommand("ABC-1234", mileage, "Juan");
    }

    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("registers mileage and publishes domain event")
        void registersMileageAndPublishesEvent() {
            when(vehicleRepository.findByPlate("ABC-1234")).thenReturn(Optional.of(activeVehicle));
            when(vehicleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(mileageLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            RegisterMileageResponse response = service.execute(commandWith(1_000L));

            assertThat(response.plate()).isEqualTo("ABC-1234");
            assertThat(response.mileageValue()).isEqualTo(1_000L);
            assertThat(response.previousMileage()).isZero();
            assertThat(response.kmTraveled()).isEqualTo(1_000L);
            verify(applicationEventPublisher, atLeastOnce()).publishEvent(any(Object.class));
        }

        @Test
        @DisplayName("flags excessive increment when over 2000 km — boundary")
        void flagsExcessiveIncrement() {
            when(vehicleRepository.findByPlate("ABC-1234")).thenReturn(Optional.of(activeVehicle));
            when(vehicleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(mileageLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            RegisterMileageResponse response = service.execute(commandWith(2_001L));

            assertThat(response.excessiveIncrement()).isTrue();
        }

        @Test
        @DisplayName("does NOT flag excessive increment at exactly 2000 km — boundary")
        void doesNotFlagAt2000() {
            when(vehicleRepository.findByPlate("ABC-1234")).thenReturn(Optional.of(activeVehicle));
            when(vehicleRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            when(mileageLogRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

            RegisterMileageResponse response = service.execute(commandWith(2_000L));

            assertThat(response.excessiveIncrement()).isFalse();
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("rejects unknown plate")
        void rejectsUnknownPlate() {
            when(vehicleRepository.findByPlate("ABC-1234")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.execute(commandWith(1_000L)))
                    .isInstanceOf(VehicleNotFoundException.class);

            verify(mileageLogRepository, never()).save(any());
            verify(applicationEventPublisher, never()).publishEvent(any());
        }

        @Test
        @DisplayName("rejects mileage of zero — boundary invalid")
        void rejectsZeroMileage() {
            when(vehicleRepository.findByPlate("ABC-1234")).thenReturn(Optional.of(activeVehicle));

            assertThatThrownBy(() -> service.execute(commandWith(0L)))
                    .isInstanceOf(InvalidMileageException.class)
                    .hasMessage("Mileage value must be greater than zero");

            verify(mileageLogRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects negative mileage")
        void rejectsNegativeMileage() {
            when(vehicleRepository.findByPlate("ABC-1234")).thenReturn(Optional.of(activeVehicle));

            assertThatThrownBy(() -> service.execute(commandWith(-1L)))
                    .isInstanceOf(InvalidMileageException.class);
        }
    }
}