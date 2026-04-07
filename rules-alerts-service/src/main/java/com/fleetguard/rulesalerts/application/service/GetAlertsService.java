package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.GetAlertsUseCase;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAlertsService implements GetAlertsUseCase {

    private final MaintenanceAlertRepositoryPort alertRepositoryPort;
    private final MaintenanceRuleQueryPort ruleQueryPort;

    @Override
    public List<AlertDetail> execute(String status) {
        List<MaintenanceAlert> alerts = (status != null && !status.isBlank())
                ? alertRepositoryPort.findByStatus(status.toUpperCase())
                : alertRepositoryPort.findAllActive();

        return alerts.stream()
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