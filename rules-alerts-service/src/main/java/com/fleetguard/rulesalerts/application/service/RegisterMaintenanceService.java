package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRecordRepositoryPort;
import com.fleetguard.rulesalerts.domain.exception.AlertNotFoundException;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.maintenance.MaintenanceRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterMaintenanceService implements RegisterMaintenanceUseCase {

    private final MaintenanceRecordRepositoryPort maintenanceRecordRepositoryPort;
    private final MaintenanceAlertRepositoryPort maintenanceAlertRepositoryPort;

    @Override
    public RegisterMaintenanceResponse execute(RegisterMaintenanceCommand command) {

        MaintenanceAlert alert = maintenanceAlertRepositoryPort.findById(command.alertId())
                .orElseThrow(() -> new AlertNotFoundException(command.alertId()));

        LocalDateTime performedAt = command.performedAt() != null
                ? command.performedAt()
                : LocalDateTime.now();

        MaintenanceRecord record = new MaintenanceRecord(
                UUID.randomUUID(),
                alert.getVehicleId(),
                command.alertId(),
                alert.getRuleId(),
                command.serviceType(),
                command.description(),
                command.cost(),
                command.provider(),
                performedAt,
                command.mileageAtService(),
                command.recordedBy()
        );

        MaintenanceRecord saved = maintenanceRecordRepositoryPort.save(record);
        log.info("MaintenanceRecord saved with id: {}", saved.getId());

        MaintenanceAlert resolved = new MaintenanceAlert(
                alert.getId(),
                alert.getVehicleId(),
                alert.getVehicleTypeId(),
                alert.getRuleId(),
                "RESOLVED",
                alert.getTriggeredAt(),
                alert.getDueAtKm()
        );
        maintenanceAlertRepositoryPort.save(resolved);
        log.info("Alert {} marked as RESOLVED", alert.getId());

        return new RegisterMaintenanceResponse(
                saved.getId(),
                saved.getVehicleId(),
                command.plate(),
                saved.getAlertId(),
                saved.getRuleId(),
                saved.getServiceType(),
                saved.getDescription(),
                saved.getCost(),
                saved.getProvider(),
                saved.getPerformedAt(),
                saved.getMileageAtService(),
                saved.getRecordedBy(),
                LocalDateTime.now()
        );
    }
}