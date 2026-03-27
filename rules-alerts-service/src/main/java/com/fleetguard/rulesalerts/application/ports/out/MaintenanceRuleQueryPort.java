package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRuleQueryPort {

    Optional<MaintenanceRule> findById(UUID id);
}
