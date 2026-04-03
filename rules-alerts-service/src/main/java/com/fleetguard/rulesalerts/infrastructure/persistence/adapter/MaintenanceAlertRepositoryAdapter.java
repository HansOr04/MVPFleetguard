package com.fleetguard.rulesalerts.infrastructure.persistence.adapter;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.infrastructure.persistence.mapper.MaintenanceAlertPersistenceMapper;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.MaintenanceAlertJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
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
    public Optional<MaintenanceAlert> findByVehicleIdAndRuleIdAndDueAtKm(UUID vehicleId, UUID ruleId, Long dueAtKm) {
        return maintenanceAlertJpaRepository
                .findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, dueAtKm)
                .map(MaintenanceAlertPersistenceMapper::toDomain);
    }

    @Override
    public List<MaintenanceAlert> findByStatus(String status) {
        return maintenanceAlertJpaRepository.findByStatus(status)
                .stream()
                .map(MaintenanceAlertPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<MaintenanceAlert> findActiveByVehicleId(UUID vehicleId) {
        return maintenanceAlertJpaRepository
                .findByVehicleIdAndStatusIn(vehicleId, List.of("PENDING", "WARNING", "OVERDUE"))
                .stream()
                .map(MaintenanceAlertPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<MaintenanceAlert> findById(UUID id) {
        return maintenanceAlertJpaRepository.findById(id)
                .map(MaintenanceAlertPersistenceMapper::toDomain);
    }

    @Override
    public List<MaintenanceAlert> findAllActive() {
        return maintenanceAlertJpaRepository
                .findByStatusIn(List.of("PENDING", "WARNING", "OVERDUE"))
                .stream()
                .map(MaintenanceAlertPersistenceMapper::toDomain)
                .toList();
    }
}