package com.fleetguard.fleet.infrastructure.persistence.repository;

import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VehicleJpaRepository extends JpaRepository<VehicleJpaEntity, UUID> {
    Optional<VehicleJpaEntity> findByPlate(String plate);
    boolean existsByPlate(String plate);
}