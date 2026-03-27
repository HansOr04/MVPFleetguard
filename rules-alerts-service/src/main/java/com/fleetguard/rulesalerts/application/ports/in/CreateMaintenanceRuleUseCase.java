package com.fleetguard.rulesalerts.application.ports.in;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CreateMaintenanceRuleUseCase {

    CreateMaintenanceRuleResponse execute(CreateMaintenanceRuleCommand command);

    record CreateMaintenanceRuleCommand(
            String name,
            String maintenanceType,
            Integer intervalKm,
            Integer warningThresholdKm
    ) {
    }

    record CreateMaintenanceRuleResponse(
            UUID id,
            String name,
            String maintenanceType,
            Integer intervalKm,
            Integer warningThresholdKm,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
