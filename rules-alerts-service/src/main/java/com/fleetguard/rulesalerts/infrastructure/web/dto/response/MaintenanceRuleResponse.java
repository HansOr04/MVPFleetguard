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
public class MaintenanceRuleResponse {

    private UUID id;

    private String name;

    private String maintenanceType;

    private Integer intervalKm;

    private Integer warningThresholdKm;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}