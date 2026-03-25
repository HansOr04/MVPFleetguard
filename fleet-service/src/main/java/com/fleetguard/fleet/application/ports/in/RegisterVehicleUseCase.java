package com.fleetguard.fleet.application.ports.in;

import java.util.UUID;

public interface RegisterVehicleUseCase {

    RegisterVehicleResponse execute(RegisterVehicleCommand command);

    record RegisterVehicleCommand(
            String plate,
            String brand,
            String model,
            int year,
            String fuelType,
            String vin,
            UUID vehicleTypeId
    ) {
    }

    record RegisterVehicleResponse(
            UUID id,
            String status
    ) {
    }
}
