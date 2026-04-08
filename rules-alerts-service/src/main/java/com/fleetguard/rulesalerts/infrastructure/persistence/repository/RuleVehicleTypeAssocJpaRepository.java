package com.fleetguard.rulesalerts.infrastructure.persistence.repository;

import com.fleetguard.rulesalerts.infrastructure.persistence.entity.RuleVehicleTypeAssocJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RuleVehicleTypeAssocJpaRepository extends JpaRepository<RuleVehicleTypeAssocJpaEntity, UUID> {

    boolean existsByRuleIdAndVehicleTypeId(UUID ruleId, UUID vehicleTypeId);

    List<RuleVehicleTypeAssocJpaEntity> findByVehicleTypeId(UUID vehicleTypeId);
}