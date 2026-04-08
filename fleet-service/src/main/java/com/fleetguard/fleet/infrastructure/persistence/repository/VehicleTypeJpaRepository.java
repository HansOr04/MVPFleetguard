package com.fleetguard.fleet.infrastructure.persistence.repository;

import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleTypeJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleTypeJpaRepository extends JpaRepository<VehicleTypeJpaEntity, UUID> {
}