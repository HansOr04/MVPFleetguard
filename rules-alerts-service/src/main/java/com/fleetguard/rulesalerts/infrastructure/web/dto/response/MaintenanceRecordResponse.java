package com.fleetguard.rulesalerts.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceRecordResponse {

    private UUID id;
    private UUID vehicleId;
    private String plate;
    private UUID alertId;
    private UUID ruleId;
    private String serviceType;
    private String description;
    private BigDecimal cost;
    private String provider;
    private LocalDateTime performedAt;
    private long mileageAtService;
    private LocalDateTime createdAt;
}