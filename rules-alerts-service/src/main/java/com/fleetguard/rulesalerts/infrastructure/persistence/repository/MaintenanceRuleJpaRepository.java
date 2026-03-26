package com.fleetguard.rulesalerts.infrastructure.persistence.repository;

import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRuleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio Spring Data JPA para MaintenanceRuleJpaEntity.
 * Sin métodos adicionales — las operaciones CRUD básicas son suficientes por ahora.
 */
public interface MaintenanceRuleJpaRepository
        extends JpaRepository<MaintenanceRuleJpaEntity, UUID> {
}
