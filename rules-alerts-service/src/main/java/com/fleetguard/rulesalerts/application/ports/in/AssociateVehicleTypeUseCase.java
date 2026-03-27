package com.fleetguard.rulesalerts.application.ports.in;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AssociateVehicleTypeUseCase {

    AssociateVehicleTypeResponse execute(AssociateVehicleTypeCommand command);

    record AssociateVehicleTypeCommand(
            UUID ruleId,
            UUID vehicleTypeId
    ) {
    }

    record AssociateVehicleTypeResponse(
            UUID id,
            UUID ruleId,
            UUID vehicleTypeId,
            LocalDateTime createdAt
    ) {
    }
}
