package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRecordRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.VehicleQueryPort;
import com.fleetguard.rulesalerts.domain.exception.InvalidMaintenanceException;
import com.fleetguard.rulesalerts.domain.exception.VehicleNotFoundException;
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
    private final VehicleQueryPort vehicleQueryPort;

    @Override
    public RegisterMaintenanceResponse execute(RegisterMaintenanceCommand command) {

        if (command.serviceType() == null || command.serviceType().isBlank()) {
            throw new InvalidMaintenanceException("El tipo de servicio es obligatorio");
        }

        if (command.performedAt() != null && command.performedAt().isAfter(LocalDateTime.now())) {
            throw new InvalidMaintenanceException("La fecha del servicio no puede ser futura");
        }

        if (command.mileageAtService() <= 0) {
            throw new InvalidMaintenanceException("El kilometraje del servicio debe ser mayor a cero");
        }

        UUID vehicleId = vehicleQueryPort.findVehicleIdByPlate(command.plate())
                .orElseThrow(() -> new VehicleNotFoundException(command.plate()));

        LocalDateTime performedAt = command.performedAt() != null
                ? command.performedAt()
                : LocalDateTime.now();

        MaintenanceRecord record = new MaintenanceRecord(
                UUID.randomUUID(),
                vehicleId,
                command.alertId(),
                command.ruleId(),
                command.serviceType(),
                command.description(),
                command.cost(),
                command.provider(),
                performedAt,
                command.mileageAtService()
        );

        MaintenanceRecord saved = maintenanceRecordRepositoryPort.save(record);
        log.info("MaintenanceRecord saved with id: {}", saved.getId());

        if (command.alertId() != null) {
            maintenanceAlertRepositoryPort.findById(command.alertId())
                    .ifPresent(alert -> {
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
                    });
        }

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
                LocalDateTime.now()
        );
    }
}