package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.RuleVehicleTypeAssocJpaEntity;

public class RuleVehicleTypeAssocPersistenceMapper {

    private RuleVehicleTypeAssocPersistenceMapper() {
    }

    public static RuleVehicleTypeAssocJpaEntity toJpaEntity(RuleVehicleTypeAssoc association) {
        return new RuleVehicleTypeAssocJpaEntity(
                association.getId(),
                association.getRuleId(),
                association.getVehicleTypeId(),
                association.getCreatedAt()
        );
    }

    public static RuleVehicleTypeAssoc toDomain(RuleVehicleTypeAssocJpaEntity entity) {
        return new RuleVehicleTypeAssoc(
                entity.getId(),
                entity.getRuleId(),
                entity.getVehicleTypeId(),
                entity.getCreatedAt()
        );
    }
}
