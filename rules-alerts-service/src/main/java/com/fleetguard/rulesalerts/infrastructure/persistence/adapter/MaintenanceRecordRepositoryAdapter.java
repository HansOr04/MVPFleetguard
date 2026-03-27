package com.fleetguard.rulesalerts.infrastructure.persistence.adapter;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRecordRepositoryPort;
import com.fleetguard.rulesalerts.domain.model.maintenance.MaintenanceRecord;
import com.fleetguard.rulesalerts.infrastructure.persistence.mapper.MaintenanceRecordPersistenceMapper;
import com.fleetguard.rulesalerts.infrastructure.persistence.repository.MaintenanceRecordJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MaintenanceRecordRepositoryAdapter implements MaintenanceRecordRepositoryPort {

    private final MaintenanceRecordJpaRepository maintenanceRecordJpaRepository;

    @Override
    public MaintenanceRecord save(MaintenanceRecord record) {
        return MaintenanceRecordPersistenceMapper.toDomain(
                maintenanceRecordJpaRepository.save(
                        MaintenanceRecordPersistenceMapper.toJpaEntity(record)));
    }
}