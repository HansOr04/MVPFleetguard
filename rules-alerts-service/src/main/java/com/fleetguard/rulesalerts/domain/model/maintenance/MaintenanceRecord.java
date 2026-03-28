package com.fleetguard.rulesalerts.domain.model.maintenance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceRecord {

    private UUID id;
    private UUID vehicleId;
    private UUID alertId;
    private UUID ruleId;
    private String serviceType;
    private String description;
    private BigDecimal cost;
    private String provider;
    private LocalDateTime performedAt;
    private long mileageAtService;
}