package com.fleetguard.rulesalerts.infrastructure.persistence.repository;

import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MaintenanceRecordJpaRepository extends JpaRepository<MaintenanceRecordJpaEntity, UUID> {
}