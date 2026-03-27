package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;

import java.util.UUID;

public interface RuleVehicleTypeAssocRepositoryPort {

    RuleVehicleTypeAssoc save(RuleVehicleTypeAssoc association);

    boolean existsByRuleIdAndVehicleTypeId(UUID ruleId, UUID vehicleTypeId);
}
