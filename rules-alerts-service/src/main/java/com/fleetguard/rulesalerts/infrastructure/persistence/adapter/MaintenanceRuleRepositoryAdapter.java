package com.fleetguard.rulesalerts.infrastructure.persistence.adapter;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleRepositoryPort;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRuleJpaEntity;
import com.fleetguard.rulesalerts.infrastructure.persistence.mapper.MaintenanceRulePersistenceMapper;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.MaintenanceRuleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MaintenanceRuleRepositoryAdapter implements MaintenanceRuleRepositoryPort {

    private final MaintenanceRuleJpaRepository maintenanceRuleJpaRepository;

    @Override
    public MaintenanceRule save(MaintenanceRule rule) {
        MaintenanceRuleJpaEntity entity = MaintenanceRulePersistenceMapper.toJpaEntity(rule);
        MaintenanceRuleJpaEntity saved = maintenanceRuleJpaRepository.save(entity);
        return MaintenanceRulePersistenceMapper.toDomain(saved);
    }

    @Override
    public boolean existsById(UUID id) {
        return maintenanceRuleJpaRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return maintenanceRuleJpaRepository.existsByName(name);
    }

    @Override
    public Optional<MaintenanceRule> findByName(String name) {
        return maintenanceRuleJpaRepository.findByName(name)
                .map(MaintenanceRulePersistenceMapper::toDomain);
    }
}