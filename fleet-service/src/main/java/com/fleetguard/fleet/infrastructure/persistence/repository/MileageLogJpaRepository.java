package com.fleetguard.fleet.infrastructure.persistence.repository;

import com.fleetguard.fleet.infrastructure.persistence.entity.MileageLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MileageLogJpaRepository extends JpaRepository<MileageLogJpaEntity, UUID> {
}
