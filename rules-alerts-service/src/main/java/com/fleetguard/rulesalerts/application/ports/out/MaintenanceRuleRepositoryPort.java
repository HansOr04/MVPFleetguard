package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;

import java.util.UUID;

public interface MaintenanceRuleRepositoryPort {

    MaintenanceRule save(MaintenanceRule rule);

    boolean existsById(UUID id);
}
