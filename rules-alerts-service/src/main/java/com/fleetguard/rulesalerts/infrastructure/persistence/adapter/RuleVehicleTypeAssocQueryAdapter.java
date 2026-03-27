package com.fleetguard.rulesalerts.infrastructure.persistence.adapter;

import com.fleetguard.rulesalerts.application.ports.out.RuleVehicleTypeAssocQueryPort;
import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import com.fleetguard.rulesalerts.infrastructure.persistence.mapper.RuleVehicleTypeAssocPersistenceMapper;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.RuleVehicleTypeAssocJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RuleVehicleTypeAssocQueryAdapter implements RuleVehicleTypeAssocQueryPort {

    private final RuleVehicleTypeAssocJpaRepository ruleVehicleTypeAssocJpaRepository;

    @Override
    public List<RuleVehicleTypeAssoc> findByVehicleTypeId(UUID vehicleTypeId) {
        return ruleVehicleTypeAssocJpaRepository.findByVehicleTypeId(vehicleTypeId)
                .stream()
                .map(RuleVehicleTypeAssocPersistenceMapper::toDomain)
                .toList();
    }
}