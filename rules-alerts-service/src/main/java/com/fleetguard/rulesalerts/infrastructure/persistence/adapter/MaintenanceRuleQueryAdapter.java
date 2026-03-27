package com.fleetguard.rulesalerts.infrastructure.persistence.adapter;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import com.fleetguard.rulesalerts.infrastructure.persistence.mapper.MaintenanceRulePersistenceMapper;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.MaintenanceRuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MaintenanceRuleQueryAdapter implements MaintenanceRuleQueryPort {

    private final MaintenanceRuleJpaRepository maintenanceRuleJpaRepository;

    @Override
    public Optional<MaintenanceRule> findById(UUID id) {
        return maintenanceRuleJpaRepository.findById(id)
                .map(MaintenanceRulePersistenceMapper::toDomain);
    }
}