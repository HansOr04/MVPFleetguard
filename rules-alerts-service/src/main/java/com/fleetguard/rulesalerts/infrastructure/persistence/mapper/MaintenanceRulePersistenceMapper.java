package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRuleJpaEntity;

public class MaintenanceRulePersistenceMapper {

    public static MaintenanceRuleJpaEntity toJpaEntity(MaintenanceRule rule) {
        return new MaintenanceRuleJpaEntity(
                rule.getId(),
                rule.getName(),
                rule.getMaintenanceType(),
                rule.getIntervalKm(),
                rule.getWarningThresholdKm(),
                rule.getStatus(),
                rule.getCreatedAt(),
                rule.getUpdatedAt()
        );
    }

    public static MaintenanceRule toDomain(MaintenanceRuleJpaEntity entity) {
        return new MaintenanceRule(
                entity.getId(),
                entity.getName(),
                entity.getMaintenanceType(),
                entity.getIntervalKm(),
                entity.getWarningThresholdKm(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}