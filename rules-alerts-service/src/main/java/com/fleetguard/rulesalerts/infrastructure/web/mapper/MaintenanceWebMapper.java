package com.fleetguard.rulesalerts.infrastructure.web.mapper;

import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceCommand;
import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.RegisterMaintenanceRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRecordResponse;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceWebMapper {

    public RegisterMaintenanceCommand toCommand(RegisterMaintenanceRequest request) {
        return new RegisterMaintenanceCommand(
                request.getVehicleId(),
                request.getAlertId(),
                request.getRuleId(),
                request.getServiceType(),
                request.getDescription(),
                request.getCost(),
                request.getProvider(),
                request.getPerformedAt(),
                request.getMileageAtService()
        );
    }

    public MaintenanceRecordResponse toResponse(RegisterMaintenanceResponse response) {
        return MaintenanceRecordResponse.builder()
                .id(response.id())
                .vehicleId(response.vehicleId())
                .alertId(response.alertId())
                .ruleId(response.ruleId())
                .serviceType(response.serviceType())
                .description(response.description())
                .cost(response.cost())
                .provider(response.provider())
                .performedAt(response.performedAt())
                .mileageAtService(response.mileageAtService())
                .createdAt(response.createdAt())
                .build();
    }
}