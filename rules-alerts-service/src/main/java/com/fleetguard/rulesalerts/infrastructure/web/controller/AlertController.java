package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.in.GetAlertsByVehicleUseCase;
import com.fleetguard.rulesalerts.application.ports.in.GetAlertsUseCase;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.EnrichedAlertResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceAlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {

    private final GetAlertsUseCase getAlertsUseCase;
    private final GetAlertsByVehicleUseCase getAlertsByVehicleUseCase;

    @GetMapping
    public ResponseEntity<List<MaintenanceAlertResponse>> getAlerts(
            @RequestParam(required = false) String status) {

        List<MaintenanceAlertResponse> response = getAlertsUseCase.execute(status)
                .stream()
                .map(d -> MaintenanceAlertResponse.builder()
                        .id(d.id())
                        .vehicleId(d.vehicleId())
                        .vehicleTypeId(d.vehicleTypeId())
                        .ruleId(d.ruleId())
                        .ruleName(d.ruleName())
                        .status(d.status())
                        .triggeredAt(d.triggeredAt())
                        .dueAtKm(d.dueAtKm())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicle/{plate}")
    public ResponseEntity<List<EnrichedAlertResponse>> getActiveAlertsByPlate(
            @PathVariable String plate) {

        List<EnrichedAlertResponse> response = getAlertsByVehicleUseCase.execute(plate)
                .stream()
                .map(d -> EnrichedAlertResponse.builder()
                        .id(d.id())
                        .vehicleId(d.vehicleId())
                        .vehicleTypeId(d.vehicleTypeId())
                        .ruleId(d.ruleId())
                        .ruleName(d.ruleName())
                        .status(d.status())
                        .triggeredAt(d.triggeredAt())
                        .dueAtKm(d.dueAtKm())
                        .build())
                .toList();
        return ResponseEntity.ok(response);
    }
}