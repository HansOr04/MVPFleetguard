package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceAlertJpaEntity;

public class MaintenanceAlertPersistenceMapper {

    private MaintenanceAlertPersistenceMapper() {
    }

    public static MaintenanceAlertJpaEntity toJpaEntity(MaintenanceAlert alert) {
        return new MaintenanceAlertJpaEntity(
                alert.getId(),
                alert.getVehicleId(),
                alert.getVehicleTypeId(),
                alert.getRuleId(),
                alert.getStatus(),
                alert.getTriggeredAt(),
                alert.getDueAtKm()
        );
    }

    public static MaintenanceAlert toDomain(MaintenanceAlertJpaEntity entity) {
        return new MaintenanceAlert(
                entity.getId(),
                entity.getVehicleId(),
                entity.getVehicleTypeId(),
                entity.getRuleId(),
                entity.getStatus(),
                entity.getTriggeredAt(),
                entity.getDueAtKm()
        );
    }
}