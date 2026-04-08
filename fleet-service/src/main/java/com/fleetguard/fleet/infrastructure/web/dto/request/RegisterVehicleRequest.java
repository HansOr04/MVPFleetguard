package com.fleetguard.fleet.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVehicleRequest {

    @NotBlank(message = "La placa es obligatoria")
    private String plate;

    @NotBlank(message = "La marca es obligatoria")
    private String brand;

    @NotBlank(message = "El modelo es obligatorio")
    private String model;

    @NotNull(message = "El año es obligatorio")
    private Integer year;

    @NotBlank(message = "El tipo de combustible es obligatorio")
    private String fuelType;

    @NotBlank(message = "El VIN es obligatorio")
    @Size(min = 17, max = 17, message = "El VIN debe tener exactamente 17 caracteres")
    private String vin;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private UUID vehicleTypeId;
}