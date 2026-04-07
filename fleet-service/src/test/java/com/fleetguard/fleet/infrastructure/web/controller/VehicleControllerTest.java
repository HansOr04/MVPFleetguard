package com.fleetguard.fleet.infrastructure.web.controller;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterVehicleRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.VehicleResponse;
import com.fleetguard.fleet.infrastructure.web.mapper.VehicleWebMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleController")
class VehicleControllerTest {

    @Mock
    private RegisterVehicleUseCase registerVehicleUseCase;

    @Mock
    private VehicleWebMapper vehicleWebMapper;

    @InjectMocks
    private VehicleController vehicleController;

    @Test
    @DisplayName("happy path — should return 201 with vehicle data")
    void shouldRegisterVehicle() {
        UUID id = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();

        RegisterVehicleRequest request = new RegisterVehicleRequest(
                "ABC-1234", "Toyota", "Hilux", 2023,
                "Diesel", "1HGCM82633A123456", typeId);

        RegisterVehicleCommand command = new RegisterVehicleCommand(
                "ABC-1234", "Toyota", "Hilux", 2023,
                "Diesel", "1HGCM82633A123456", typeId);

        RegisterVehicleResponse serviceResponse = new RegisterVehicleResponse(
                id, "ABC-1234", "Toyota", "Hilux", 2023,
                "Diesel", "1HGCM82633A123456", "ACTIVE", 0, "Pickup");

        VehicleResponse vehicleResponse = new VehicleResponse(
                id, "ABC-1234", "Toyota", "Hilux", 2023,
                "Diesel", "1HGCM82633A123456", "ACTIVE", 0, "Pickup");

        when(vehicleWebMapper.toCommand(request)).thenReturn(command);
        when(registerVehicleUseCase.execute(command)).thenReturn(serviceResponse);
        when(vehicleWebMapper.toResponse(serviceResponse)).thenReturn(vehicleResponse);

        ResponseEntity<VehicleResponse> result = vehicleController.registerVehicle(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isEqualTo(vehicleResponse);
        verify(registerVehicleUseCase).execute(command);
    }
}