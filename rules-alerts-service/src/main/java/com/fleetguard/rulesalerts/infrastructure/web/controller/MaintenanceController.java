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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final RegisterMaintenanceUseCase registerMaintenanceUseCase;
    private final MaintenanceWebMapper maintenanceWebMapper;

    @PostMapping("/{plate}")
    public ResponseEntity<MaintenanceRecordResponse> registerMaintenance(
            @PathVariable String plate,
            @Valid @RequestBody RegisterMaintenanceRequest request) {

        RegisterMaintenanceCommand command = maintenanceWebMapper.toCommand(plate.toUpperCase(), request);
        RegisterMaintenanceResponse result = registerMaintenanceUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(maintenanceWebMapper.toResponse(result));
    }
}