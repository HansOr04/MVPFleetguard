package com.fleetguard.rulesalerts.infrastructure.web.mapper;

import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeCommand;
import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.AssociateVehicleTypeRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.RuleVehicleTypeAssocResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AssociateVehicleTypeWebMapper {

    public AssociateVehicleTypeCommand toCommand(UUID ruleId, AssociateVehicleTypeRequest request) {
        return new AssociateVehicleTypeCommand(
                ruleId,
                request.getVehicleTypeId()
        );
    }

    public RuleVehicleTypeAssocResponse toResponse(AssociateVehicleTypeResponse response) {
        return RuleVehicleTypeAssocResponse.builder()
                .id(response.id())
                .ruleId(response.ruleId())
                .vehicleTypeId(response.vehicleTypeId())
                .createdAt(response.createdAt())
                .build();
    }
}
