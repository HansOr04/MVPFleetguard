package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.application.ports.out.RuleVehicleTypeAssocQueryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluateMaintenanceAlertsService {

    private final RuleVehicleTypeAssocQueryPort assocQueryPort;
    private final MaintenanceRuleQueryPort ruleQueryPort;
    private final MaintenanceAlertRepositoryPort alertRepositoryPort;

    public void evaluate(UUID vehicleId, UUID vehicleTypeId, long currentMileage, String vehicleStatus) {

        if (!"ACTIVE".equals(vehicleStatus)) {
            log.info("Vehicle {} is not ACTIVE (status: {}). Skipping alert evaluation.", vehicleId, vehicleStatus);
            return;
        }

        List<RuleVehicleTypeAssoc> associations = assocQueryPort.findByVehicleTypeId(vehicleTypeId);

        if (associations.isEmpty()) {
            log.info("No maintenance rules associated with vehicleTypeId: {}", vehicleTypeId);
            return;
        }

        for (RuleVehicleTypeAssoc association : associations) {
            Optional<MaintenanceRule> ruleOpt = ruleQueryPort.findById(association.getRuleId());

            if (ruleOpt.isEmpty()) {
                log.warn("MaintenanceRule not found for ruleId: {}", association.getRuleId());
                continue;
            }

            MaintenanceRule rule = ruleOpt.get();
            long dueAtKm = rule.getIntervalKm();
            long kmRemaining = dueAtKm - currentMileage;

            if (kmRemaining > rule.getWarningThresholdKm()) {
                continue;
            }

            if (alertRepositoryPort.existsByVehicleIdAndRuleIdAndStatus(vehicleId, rule.getId(), "PENDING")) {
                log.info("PENDING alert already exists for vehicleId: {}, ruleId: {}", vehicleId, rule.getId());
                continue;
            }

            MaintenanceAlert alert = new MaintenanceAlert(
                    UUID.randomUUID(),
                    vehicleId,
                    vehicleTypeId,
                    rule.getId(),
                    "PENDING",
                    LocalDateTime.now(),
                    dueAtKm
            );

            alertRepositoryPort.save(alert);
            log.info("Alert generated for vehicleId: {}, ruleId: {}, dueAtKm: {}", vehicleId, rule.getId(), dueAtKm);
        }
    }
}
