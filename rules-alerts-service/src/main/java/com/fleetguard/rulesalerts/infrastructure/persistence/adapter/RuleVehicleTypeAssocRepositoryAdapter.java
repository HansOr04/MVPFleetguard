package com.fleetguard.rulesalerts.infrastructure.persistence.adapter;

import com.fleetguard.rulesalerts.application.ports.out.RuleVehicleTypeAssocRepositoryPort;
import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.RuleVehicleTypeAssocJpaEntity;
import com.fleetguard.rulesalerts.infrastructure.persistence.mapper.RuleVehicleTypeAssocPersistenceMapper;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.RuleVehicleTypeAssocJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RuleVehicleTypeAssocRepositoryAdapter implements RuleVehicleTypeAssocRepositoryPort {

    private final RuleVehicleTypeAssocJpaRepository ruleVehicleTypeAssocJpaRepository;

    @Override
    public RuleVehicleTypeAssoc save(RuleVehicleTypeAssoc association) {
        RuleVehicleTypeAssocJpaEntity entity = RuleVehicleTypeAssocPersistenceMapper.toJpaEntity(association);
        RuleVehicleTypeAssocJpaEntity saved = ruleVehicleTypeAssocJpaRepository.save(entity);
        return RuleVehicleTypeAssocPersistenceMapper.toDomain(saved);
    }

    @Override
    public boolean existsByRuleIdAndVehicleTypeId(UUID ruleId, UUID vehicleTypeId) {
        return ruleVehicleTypeAssocJpaRepository.existsByRuleIdAndVehicleTypeId(ruleId, vehicleTypeId);
    }
}
