package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleCommand;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.MaintenanceRuleWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maintenance-rules")
public class MaintenanceRuleController {

    private final CreateMaintenanceRuleUseCase createMaintenanceRuleUseCase;
    private final MaintenanceRuleWebMapper maintenanceRuleWebMapper;

    @PostMapping
    public ResponseEntity<MaintenanceRuleResponse> create(
            @Valid @RequestBody CreateMaintenanceRuleRequest request) {

        CreateMaintenanceRuleCommand command = maintenanceRuleWebMapper.toCommand(request);
        CreateMaintenanceRuleResponse result = createMaintenanceRuleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(maintenanceRuleWebMapper.toResponse(result));
    }
}
