package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase;
import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeCommand;
import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeResponse;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleCommand;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.AssociateVehicleTypeRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.RuleVehicleTypeAssocResponse;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.AssociateVehicleTypeWebMapper;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.MaintenanceRuleWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maintenance-rules")
public class MaintenanceRuleController {

    private final CreateMaintenanceRuleUseCase createMaintenanceRuleUseCase;
    private final MaintenanceRuleWebMapper maintenanceRuleWebMapper;
    private final AssociateVehicleTypeUseCase associateVehicleTypeUseCase;
    private final AssociateVehicleTypeWebMapper associateVehicleTypeWebMapper;

    @PostMapping
    public ResponseEntity<MaintenanceRuleResponse> create(
            @Valid @RequestBody CreateMaintenanceRuleRequest request) {

        CreateMaintenanceRuleCommand command = maintenanceRuleWebMapper.toCommand(request);
        CreateMaintenanceRuleResponse result = createMaintenanceRuleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(maintenanceRuleWebMapper.toResponse(result));
    }

    @PostMapping("/{id}/vehicle-types")
    public ResponseEntity<RuleVehicleTypeAssocResponse> associateVehicleType(
            @PathVariable UUID id,
            @Valid @RequestBody AssociateVehicleTypeRequest request) {

        AssociateVehicleTypeCommand command = associateVehicleTypeWebMapper.toCommand(id, request);
        AssociateVehicleTypeResponse result = associateVehicleTypeUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(associateVehicleTypeWebMapper.toResponse(result));
    }
}
