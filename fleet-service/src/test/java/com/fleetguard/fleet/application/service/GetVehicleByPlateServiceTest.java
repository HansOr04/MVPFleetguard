package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase.GetVehicleByPlateResponse;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetVehicleByPlateService")
class GetVehicleByPlateServiceTest {

    @Mock
    private VehicleRepositoryPort vehicleRepository;

    @InjectMocks
    private GetVehicleByPlateService service;

    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        VehicleType type = new VehicleType(UUID.randomUUID(), "Pickup", "Pickup truck");
        vehicle = Vehicle.create(
                new Plate("ABC-1234"), "Toyota", "Hilux",
                2023, "Diesel", new Vin("1HGCM82633A123456"), type);
    }

    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("returns vehicle data when plate exists")
        void returnsVehicleWhenPlateExists() {
            when(vehicleRepository.findByPlate("ABC-1234")).thenReturn(Optional.of(vehicle));

            GetVehicleByPlateResponse response = service.execute("ABC-1234");

            assertThat(response.plate()).isEqualTo("ABC-1234");
            assertThat(response.brand()).isEqualTo("Toyota");
            assertThat(response.model()).isEqualTo("Hilux");
            assertThat(response.year()).isEqualTo(2023);
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.vehicleTypeName()).isEqualTo("Pickup");
            assertThat(response.currentMileage()).isZero();
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("throws VehicleNotFoundException when plate does not exist")
        void throwsWhenPlateNotFound() {
            when(vehicleRepository.findByPlate("ZZZ-9999")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.execute("ZZZ-9999"))
                    .isInstanceOf(VehicleNotFoundException.class);
        }
    }
}