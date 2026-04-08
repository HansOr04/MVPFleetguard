package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.GetAlertsByVehicleUseCase;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.application.ports.out.VehicleQueryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetAlertsByVehicleService implements GetAlertsByVehicleUseCase {

    private final VehicleQueryPort vehicleQueryPort;
    private final MaintenanceAlertRepositoryPort alertRepositoryPort;
    private final MaintenanceRuleQueryPort ruleQueryPort;

    @Override
    public List<AlertDetail> execute(String plate) {
        UUID vehicleId = vehicleQueryPort.findVehicleIdByPlate(plate.toUpperCase())
                .orElse(null);

        if (vehicleId == null) {
            return List.of();
        }

        return alertRepositoryPort.findActiveByVehicleId(vehicleId)
                .stream()
                .map(this::toDetail)
                .toList();
    }

    private AlertDetail toDetail(MaintenanceAlert alert) {
        String ruleName = ruleQueryPort.findById(alert.getRuleId())
                .map(MaintenanceRule::getName)
                .orElse("Regla desconocida");

        return new AlertDetail(
                alert.getId(),
                alert.getVehicleId(),
                alert.getVehicleTypeId(),
                alert.getRuleId(),
                ruleName,
                alert.getStatus(),
                alert.getTriggeredAt(),
                alert.getDueAtKm()
        );
    }
}