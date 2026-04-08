package com.fleetguard.rulesalerts.infrastructure.persistence.repository;

import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceAlertJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MaintenanceAlertJpaRepository extends JpaRepository<MaintenanceAlertJpaEntity, UUID> {

    Optional<MaintenanceAlertJpaEntity> findByVehicleIdAndRuleIdAndDueAtKm(UUID vehicleId, UUID ruleId, Long dueAtKm);

    List<MaintenanceAlertJpaEntity> findByStatus(String status);

    List<MaintenanceAlertJpaEntity> findByVehicleIdAndStatusIn(UUID vehicleId, List<String> statuses);

    List<MaintenanceAlertJpaEntity> findByStatusIn(List<String> statuses);
}