package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;

import java.util.List;
import java.util.UUID;

public interface MaintenanceAlertRepositoryPort {

    MaintenanceAlert save(MaintenanceAlert alert);

    boolean existsByVehicleIdAndRuleIdAndStatus(UUID vehicleId, UUID ruleId, String status);

    List<MaintenanceAlert> findByStatus(String status);

    List<MaintenanceAlert> findActiveByVehicleId(UUID vehicleId);
}