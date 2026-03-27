package com.fleetguard.rulesalerts.infrastructure.persistence.adapter;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.infrastructure.persistence.mapper.MaintenanceAlertPersistenceMapper;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.MaintenanceAlertJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MaintenanceAlertRepositoryAdapter implements MaintenanceAlertRepositoryPort {

    private final MaintenanceAlertJpaRepository maintenanceAlertJpaRepository;

    @Override
    public MaintenanceAlert save(MaintenanceAlert alert) {
        return MaintenanceAlertPersistenceMapper.toDomain(
                maintenanceAlertJpaRepository.save(
                        MaintenanceAlertPersistenceMapper.toJpaEntity(alert)));
    }

    @Override
    public boolean existsByVehicleIdAndRuleIdAndStatus(UUID vehicleId, UUID ruleId, String status) {
        return maintenanceAlertJpaRepository.existsByVehicleIdAndRuleIdAndStatus(vehicleId, ruleId, status);
    }

    @Override
    public List<MaintenanceAlert> findByStatus(String status) {
        return maintenanceAlertJpaRepository.findByStatus(status)
                .stream()
                .map(MaintenanceAlertPersistenceMapper::toDomain)
                .toList();
    }
}