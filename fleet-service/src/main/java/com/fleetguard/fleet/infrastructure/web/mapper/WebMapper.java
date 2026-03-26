package com.fleetguard.fleet.infrastructure.web.mapper;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterVehicleRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.VehicleResponse;
import org.springframework.stereotype.Component;

@Component
public class WebMapper {

    public RegisterVehicleCommand toCommand(RegisterVehicleRequest request) {
        return new RegisterVehicleCommand(
                request.getPlate(),
                request.getBrand(),
                request.getModel(),
                request.getYear(),
                request.getFuelType(),
                request.getVin(),
                request.getVehicleTypeId()
        );
    }

    public VehicleResponse toResponse(RegisterVehicleResponse response) {
        return new VehicleResponse(
                response.id(),
                response.plate(),
                response.brand(),
                response.model(),
                response.year(),
                response.fuelType(),
                response.vin(),
                response.status(),
                response.currentMileage(),
                response.vehicleTypeName()
        );
    }
}
