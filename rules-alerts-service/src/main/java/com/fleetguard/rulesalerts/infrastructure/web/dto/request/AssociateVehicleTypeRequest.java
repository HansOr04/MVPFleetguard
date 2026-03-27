package com.fleetguard.rulesalerts.infrastructure.web.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssociateVehicleTypeRequest {

    @NotNull(message = "El tipo de vehículo es obligatorio.")
    private UUID vehicleTypeId;
}
