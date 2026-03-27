package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;

public interface MaintenanceRuleRepositoryPort {

    MaintenanceRule save(MaintenanceRule rule);
}
