package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceAlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {

    private final MaintenanceAlertRepositoryPort maintenanceAlertRepositoryPort;

    @GetMapping
    public ResponseEntity<List<MaintenanceAlertResponse>> getAlerts(
            @RequestParam(required = false) String status) {

        List<MaintenanceAlert> alerts = (status != null && !status.isBlank())
                ? maintenanceAlertRepositoryPort.findByStatus(status.toUpperCase())
                : maintenanceAlertRepositoryPort.findAllActive();

        List<MaintenanceAlertResponse> response = alerts.stream()
                .map(a -> MaintenanceAlertResponse.builder()
                        .id(a.getId())
                        .vehicleId(a.getVehicleId())
                        .vehicleTypeId(a.getVehicleTypeId())
                        .ruleId(a.getRuleId())
                        .status(a.getStatus())
                        .triggeredAt(a.getTriggeredAt())
                        .dueAtKm(a.getDueAtKm())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }
}