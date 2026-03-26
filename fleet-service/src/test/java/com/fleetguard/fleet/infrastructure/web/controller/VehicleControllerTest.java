package com.fleetguard.fleet.infrastructure.web.controller;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterVehicleRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.VehicleResponse;
import com.fleetguard.fleet.infrastructure.web.mapper.VehicleWebMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleControllerTest {

    @Mock
    private RegisterVehicleUseCase registerVehicleUseCase;

    @Mock
    private VehicleWebMapper mapper;

    @InjectMocks
    private VehicleController vehicleController;

    @Test
    void shouldRegisterVehicle() {
        UUID id = UUID.randomUUID();
        RegisterVehicleRequest request = new RegisterVehicleRequest(
                "ABC123", "Toyota", "Corolla", 2023,
                "Gasoline", "1HGCM82633A123456", UUID.randomUUID()
        );
        RegisterVehicleCommand command = new RegisterVehicleCommand(
                request.getPlate(), request.getBrand(), request.getModel(),
                request.getYear(), request.getFuelType(), request.getVin(),
                request.getVehicleTypeId()
        );
        RegisterVehicleResponse response = new RegisterVehicleResponse(
                id, command.plate(), command.brand(), command.model(),
                command.year(), command.fuelType(), command.vin(),
                "ACTIVE", 0, "Sedan"
        );
        VehicleResponse vehicleResponse = new VehicleResponse(
                id, response.plate(), response.brand(), response.model(),
                response.year(), response.fuelType(), response.vin(),
                response.status(), response.currentMileage(),
                response.vehicleTypeName()
        );

        MockitoAnnotations.openMocks(this);
        when(mapper.toCommand(request)).thenReturn(command);
        when(registerVehicleUseCase.execute(command)).thenReturn(response);
        when(mapper.toResponse(response)).thenReturn(vehicleResponse);

        ResponseEntity<VehicleResponse> result = vehicleController.registerVehicle(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(vehicleResponse, result.getBody());
        verify(mapper, times(1)).toCommand(request);
        verify(registerVehicleUseCase, times(1)).execute(command);
        verify(mapper, times(1)).toResponse(response);
    }
}