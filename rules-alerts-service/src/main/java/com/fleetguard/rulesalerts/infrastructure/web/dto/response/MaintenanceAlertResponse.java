package com.fleetguard.rulesalerts.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceAlertResponse {

    private UUID id;
    private UUID vehicleId;
    private UUID vehicleTypeId;
    private UUID ruleId;
    private String ruleName;
    private String status;
    private LocalDateTime triggeredAt;
    private Long dueAtKm;
}