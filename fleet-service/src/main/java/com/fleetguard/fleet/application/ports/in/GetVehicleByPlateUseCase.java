package com.fleetguard.fleet.application.ports.in;

import java.util.UUID;

public interface GetVehicleByPlateUseCase {

    GetVehicleByPlateResponse execute(String plate);

    record GetVehicleByPlateResponse(
            UUID id,
            String plate,
            String brand,
            String model,
            int year,
            String fuelType,
            String vin,
            String status,
            long currentMileage,
            String vehicleTypeName
    ) {}
}