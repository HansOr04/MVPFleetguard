package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase;
import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceCommand;
import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.RegisterMaintenanceRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRecordResponse;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.MaintenanceWebMapper;
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
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final RegisterMaintenanceUseCase registerMaintenanceUseCase;
    private final MaintenanceWebMapper maintenanceWebMapper;

    @PostMapping
    public ResponseEntity<MaintenanceRecordResponse> registerMaintenance(
            @Valid @RequestBody RegisterMaintenanceRequest request) {

        RegisterMaintenanceCommand command = maintenanceWebMapper.toCommand(request);
        RegisterMaintenanceResponse result = registerMaintenanceUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(maintenanceWebMapper.toResponse(result));
    }
}