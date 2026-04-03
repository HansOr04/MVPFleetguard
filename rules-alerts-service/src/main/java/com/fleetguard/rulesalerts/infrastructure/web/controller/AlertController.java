package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.application.ports.out.VehicleQueryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.EnrichedAlertResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceAlertResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alerts")
public class AlertController {

    private final MaintenanceAlertRepositoryPort maintenanceAlertRepositoryPort;
    private final VehicleQueryPort vehicleQueryPort;
    private final MaintenanceRuleQueryPort maintenanceRuleQueryPort;

    @GetMapping
    public ResponseEntity<List<MaintenanceAlertResponse>> getAlerts(
            @RequestParam(required = false) String status) {

        List<MaintenanceAlert> alerts = (status != null && !status.isBlank())
                ? maintenanceAlertRepositoryPort.findByStatus(status.toUpperCase())
                : maintenanceAlertRepositoryPort.findAllActive();

        List<MaintenanceAlertResponse> response = alerts.stream()
                .map(a -> {
                    String ruleName = maintenanceRuleQueryPort.findById(a.getRuleId())
                            .map(MaintenanceRule::getName)
                            .orElse("Regla desconocida");

                    return MaintenanceAlertResponse.builder()
                            .id(a.getId())
                            .vehicleId(a.getVehicleId())
                            .vehicleTypeId(a.getVehicleTypeId())
                            .ruleId(a.getRuleId())
                            .ruleName(ruleName)
                            .status(a.getStatus())
                            .triggeredAt(a.getTriggeredAt())
                            .dueAtKm(a.getDueAtKm())
                            .build();
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/vehicle/{plate}")
    public ResponseEntity<List<EnrichedAlertResponse>> getActiveAlertsByPlate(
            @PathVariable String plate) {

        UUID vehicleId = vehicleQueryPort.findVehicleIdByPlate(plate.toUpperCase())
                .orElse(null);

        if (vehicleId == null) {
            return ResponseEntity.ok(List.of());
        }

        List<MaintenanceAlert> alerts = maintenanceAlertRepositoryPort.findActiveByVehicleId(vehicleId);

        List<EnrichedAlertResponse> response = alerts.stream()
                .map(a -> {
                    String ruleName = maintenanceRuleQueryPort.findById(a.getRuleId())
                            .map(MaintenanceRule::getName)
                            .orElse("Regla desconocida");

                    return EnrichedAlertResponse.builder()
                            .id(a.getId())
                            .vehicleId(a.getVehicleId())
                            .vehicleTypeId(a.getVehicleTypeId())
                            .ruleId(a.getRuleId())
                            .ruleName(ruleName)
                            .status(a.getStatus())
                            .triggeredAt(a.getTriggeredAt())
                            .dueAtKm(a.getDueAtKm())
                            .build();
                })
                .toList();

        return ResponseEntity.ok(response);
    }
}