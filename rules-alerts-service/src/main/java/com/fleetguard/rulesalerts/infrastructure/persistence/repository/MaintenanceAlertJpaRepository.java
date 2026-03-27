package com.fleetguard.rulesalerts.infrastructure.persistence.repository;

import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceAlertJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaintenanceAlertJpaRepository extends JpaRepository<MaintenanceAlertJpaEntity, UUID> {

    boolean existsByVehicleIdAndRuleIdAndStatus(UUID vehicleId, UUID ruleId, String status);

    List<MaintenanceAlertJpaEntity> findByStatus(String status);
}