package com.fleetguard.fleet.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private UUID id;
    private String plate;
    private String brand;
    private String model;
    private int year;
    private String fuelType;
    private String vin;
    private String status;
    private long currentMileage;
    private String vehicleTypeName;
}
