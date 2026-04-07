package com.fleetguard.rulesalerts.domain.model.maintenance;

import com.fleetguard.rulesalerts.domain.exception.InvalidMaintenanceException;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class MaintenanceRecord {

    private final UUID id;
    private final UUID vehicleId;
    private final UUID alertId;
    private final UUID ruleId;
    private final String serviceType;
    private final String description;
    private final BigDecimal cost;
    private final String provider;
    private final LocalDateTime performedAt;
    private final long mileageAtService;
    private final String recordedBy;

    public MaintenanceRecord(UUID id, UUID vehicleId, UUID alertId, UUID ruleId,
                             String serviceType, String description, BigDecimal cost,
                             String provider, LocalDateTime performedAt,
                             long mileageAtService, String recordedBy) {
        validate(serviceType, performedAt, mileageAtService, recordedBy);

        this.id = id;
        this.vehicleId = vehicleId;
        this.alertId = alertId;
        this.ruleId = ruleId;
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
        this.provider = provider;
        this.performedAt = performedAt;
        this.mileageAtService = mileageAtService;
        this.recordedBy = recordedBy;
    }

    private MaintenanceRecord(UUID id, UUID vehicleId, UUID alertId, UUID ruleId,
                              String serviceType, String description, BigDecimal cost,
                              String provider, LocalDateTime performedAt,
                              long mileageAtService, String recordedBy,
                              boolean reconstitute) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.alertId = alertId;
        this.ruleId = ruleId;
        this.serviceType = serviceType;
        this.description = description;
        this.cost = cost;
        this.provider = provider;
        this.performedAt = performedAt;
        this.mileageAtService = mileageAtService;
        this.recordedBy = recordedBy;
    }

    public static MaintenanceRecord reconstitute(UUID id, UUID vehicleId, UUID alertId,
                                                 UUID ruleId, String serviceType,
                                                 String description, BigDecimal cost,
                                                 String provider, LocalDateTime performedAt,
                                                 long mileageAtService, String recordedBy) {
        return new MaintenanceRecord(id, vehicleId, alertId, ruleId, serviceType,
                description, cost, provider, performedAt, mileageAtService, recordedBy, true);
    }

    private static void validate(String serviceType, LocalDateTime performedAt,
                                 long mileageAtService, String recordedBy) {
        if (serviceType == null || serviceType.isBlank()) {
            throw new InvalidMaintenanceException("El tipo de servicio es obligatorio");
        }
        if (performedAt != null && performedAt.isAfter(LocalDateTime.now())) {
            throw new InvalidMaintenanceException("La fecha del servicio no puede ser futura");
        }
        if (mileageAtService <= 0) {
            throw new InvalidMaintenanceException("El kilometraje del servicio debe ser mayor a cero");
        }
        if (recordedBy == null || recordedBy.isBlank()) {
            throw new InvalidMaintenanceException("El nombre de quien registra es obligatorio");
        }
    }
}