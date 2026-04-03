package com.fleetguard.rulesalerts.application.ports.out;

import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceAlertRepositoryPort {

    MaintenanceAlert save(MaintenanceAlert alert);

    Optional<MaintenanceAlert> findByVehicleIdAndRuleIdAndDueAtKm(UUID vehicleId, UUID ruleId, Long dueAtKm);

    List<MaintenanceAlert> findByStatus(String status);

    List<MaintenanceAlert> findActiveByVehicleId(UUID vehicleId);

    Optional<MaintenanceAlert> findById(UUID id);

    List<MaintenanceAlert> findAllActive();
}