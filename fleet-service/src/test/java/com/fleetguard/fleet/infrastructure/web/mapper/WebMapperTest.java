package com.fleetguard.fleet.infrastructure.web.mapper;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterVehicleRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.VehicleResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WebMapperTest {

    @Test
    void shouldMapToCommand() {
        RegisterVehicleRequest request = new RegisterVehicleRequest(
                "ABC123", "Toyota", "Corolla", 2023,
                "Gasoline", "1HGCM82633A123456", UUID.randomUUID()
        );

        WebMapper mapper = new WebMapper();
        RegisterVehicleCommand command = mapper.toCommand(request);

        assertEquals(request.getPlate(), command.plate());
        assertEquals(request.getBrand(), command.brand());
        assertEquals(request.getModel(), command.model());
        assertEquals(request.getYear(), command.year());
        assertEquals(request.getFuelType(), command.fuelType());
        assertEquals(request.getVin(), command.vin());
        assertEquals(request.getVehicleTypeId(), command.vehicleTypeId());
    }

    @Test
    void shouldMapToResponse() {
        RegisterVehicleResponse response = new RegisterVehicleResponse(
                UUID.randomUUID(), "ABC123", "Toyota", "Corolla", 2023,
                "Gasoline", "1HGCM82633A123456", "ACTIVE", 0, "Sedan"
        );

        WebMapper mapper = new WebMapper();
        VehicleResponse vehicleResponse = mapper.toResponse(response);

        assertEquals(response.id(), vehicleResponse.getId());
        assertEquals(response.plate(), vehicleResponse.getPlate());
        assertEquals(response.brand(), vehicleResponse.getBrand());
        assertEquals(response.model(), vehicleResponse.getModel());
        assertEquals(response.year(), vehicleResponse.getYear());
        assertEquals(response.fuelType(), vehicleResponse.getFuelType());
        assertEquals(response.vin(), vehicleResponse.getVin());
        assertEquals(response.status(), vehicleResponse.getStatus());
        assertEquals(response.currentMileage(), vehicleResponse.getCurrentMileage());
        assertEquals(response.vehicleTypeName(), vehicleResponse.getVehicleTypeName());
    }
}