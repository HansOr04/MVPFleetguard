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
            long interval = rule.getIntervalKm();
            long dueAtKm = ((currentMileage / interval) + 1) * interval;
            long kmRemaining = dueAtKm - currentMileage;

            if (kmRemaining > rule.getWarningThresholdKm()) {
                log.info("Vehicle {} is not near maintenance for ruleId: {}. kmRemaining: {}", vehicleId, rule.getId(), kmRemaining);
                evaluatePreviousCycle(vehicleId, rule.getId(), currentMileage, interval, dueAtKm);
                continue;
            }

            String newStatus = resolveStatus(kmRemaining, rule.getWarningThresholdKm());

            Optional<MaintenanceAlert> existingOpt =
                    alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, rule.getId(), dueAtKm);

            if (existingOpt.isPresent()) {
                MaintenanceAlert existing = existingOpt.get();
                if ("RESOLVED".equals(existing.getStatus())) {
                    log.info("Alert for cycle dueAtKm={} already RESOLVED for vehicleId: {}, ruleId: {}", dueAtKm, vehicleId, rule.getId());
                    continue;
                }
                if (existing.getStatus().equals(newStatus)) {
                    log.info("Alert already up-to-date for vehicleId: {}, ruleId: {}, dueAtKm: {}", vehicleId, rule.getId(), dueAtKm);
                    continue;
                }
                alertRepositoryPort.save(new MaintenanceAlert(
                        existing.getId(),
                        existing.getVehicleId(),
                        existing.getVehicleTypeId(),
                        existing.getRuleId(),
                        newStatus,
                        existing.getTriggeredAt(),
                        dueAtKm
                ));
                log.info("Alert updated to status={}, dueAtKm={} for vehicleId: {}, ruleId: {}",
                        newStatus, dueAtKm, vehicleId, rule.getId());
                continue;
            }

            alertRepositoryPort.save(new MaintenanceAlert(
                    UUID.randomUUID(),
                    vehicleId,
                    vehicleTypeId,
                    rule.getId(),
                    newStatus,
                    LocalDateTime.now(),
                    dueAtKm
            ));
            log.info("Alert created with status={}, dueAtKm={} for vehicleId: {}, ruleId: {}",
                    newStatus, dueAtKm, vehicleId, rule.getId());
        }
    }

    private void evaluatePreviousCycle(UUID vehicleId, UUID ruleId, long currentMileage, long interval, long dueAtKm) {
        long previousDueAtKm = dueAtKm - interval;
        if (previousDueAtKm <= 0) return;

        alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, previousDueAtKm)
                .ifPresent(prev -> {
                    if ("RESOLVED".equals(prev.getStatus()) || "OVERDUE".equals(prev.getStatus())) return;
                    if (currentMileage >= previousDueAtKm) {
                        alertRepositoryPort.save(new MaintenanceAlert(
                                prev.getId(),
                                prev.getVehicleId(),
                                prev.getVehicleTypeId(),
                                prev.getRuleId(),
                                "OVERDUE",
                                prev.getTriggeredAt(),
                                prev.getDueAtKm()
                        ));
                        log.info("Previous cycle marked as OVERDUE for vehicleId: {}, ruleId: {}, dueAtKm: {}",
                                vehicleId, ruleId, previousDueAtKm);
                    }
                });
    }

    private String resolveStatus(long kmRemaining, long warningThresholdKm) {
        if (kmRemaining <= 0) {
            return "OVERDUE";
        }
        if (kmRemaining <= warningThresholdKm / 3) {
            return "WARNING";
        }
        return "PENDING";
    }
}