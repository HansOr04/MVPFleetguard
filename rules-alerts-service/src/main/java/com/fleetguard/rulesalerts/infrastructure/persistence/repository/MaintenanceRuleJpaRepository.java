package com.fleetguard.rulesalerts.infrastructure.persistence.repository;

import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaintenanceRuleJpaRepository extends JpaRepository<MaintenanceRuleJpaEntity, UUID> {

    boolean existsByName(String name);

    Optional<MaintenanceRuleJpaEntity> findByName(String name);
}