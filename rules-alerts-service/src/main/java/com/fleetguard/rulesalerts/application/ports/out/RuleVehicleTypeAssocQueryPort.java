package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;

import java.util.List;
import java.util.UUID;

public interface RuleVehicleTypeAssocQueryPort {

    List<RuleVehicleTypeAssoc> findByVehicleTypeId(UUID vehicleTypeId);
}