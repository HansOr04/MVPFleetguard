package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.maintenance.MaintenanceRecord;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRecordJpaEntity;

public class MaintenanceRecordPersistenceMapper {

    private MaintenanceRecordPersistenceMapper() {
    }

    public static MaintenanceRecordJpaEntity toJpaEntity(MaintenanceRecord record) {
        return new MaintenanceRecordJpaEntity(
                record.getId(),
                record.getVehicleId(),
                record.getAlertId(),
                record.getRuleId(),
                record.getServiceType(),
                record.getDescription(),
                record.getCost(),
                record.getProvider(),
                record.getPerformedAt(),
                record.getMileageAtService()
        );
    }

    public static MaintenanceRecord toDomain(MaintenanceRecordJpaEntity entity) {
        return new MaintenanceRecord(
                entity.getId(),
                entity.getVehicleId(),
                entity.getAlertId(),
                entity.getRuleId(),
                entity.getServiceType(),
                entity.getDescription(),
                entity.getCost(),
                entity.getProvider(),
                entity.getPerformedAt(),
                entity.getMileageAtService()
        );
    }
}