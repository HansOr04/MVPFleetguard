package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRuleRepositoryPort {

    MaintenanceRule save(MaintenanceRule rule);

    boolean existsById(UUID id);

    boolean existsByName(String name);

    Optional<MaintenanceRule> findByName(String name);
}