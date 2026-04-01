package com.fleetguard.rulesalerts.application.ports.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public interface RegisterMaintenanceUseCase {

    RegisterMaintenanceResponse execute(RegisterMaintenanceCommand command);

    record RegisterMaintenanceCommand(
            String plate,
            UUID alertId,
            UUID ruleId,
            String serviceType,
            String description,
            BigDecimal cost,
            String provider,
            LocalDateTime performedAt,
            long mileageAtService
    ) {
    }

    record RegisterMaintenanceResponse(
            UUID id,
            UUID vehicleId,
            String plate,
            UUID alertId,
            UUID ruleId,
            String serviceType,
            String description,
            BigDecimal cost,
            String provider,
            LocalDateTime performedAt,
            long mileageAtService,
            LocalDateTime createdAt
    ) {
    }
}