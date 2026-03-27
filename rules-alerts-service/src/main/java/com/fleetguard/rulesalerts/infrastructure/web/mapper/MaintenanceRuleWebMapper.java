package com.fleetguard.rulesalerts.infrastructure.web.mapper;

import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleCommand;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceRuleWebMapper {

    public CreateMaintenanceRuleCommand toCommand(CreateMaintenanceRuleRequest request) {
        return new CreateMaintenanceRuleCommand(
                request.getName(),
                request.getMaintenanceType(),
                request.getIntervalKm(),
                request.getWarningThresholdKm()
        );
    }

    public MaintenanceRuleResponse toResponse(CreateMaintenanceRuleResponse response) {
        return MaintenanceRuleResponse.builder()
                .id(response.id())
                .name(response.name())
                .maintenanceType(response.maintenanceType())
                .intervalKm(response.intervalKm())
                .warningThresholdKm(response.warningThresholdKm())
                .status(response.status())
                .createdAt(response.createdAt())
                .updatedAt(response.updatedAt())
                .build();
    }
}
