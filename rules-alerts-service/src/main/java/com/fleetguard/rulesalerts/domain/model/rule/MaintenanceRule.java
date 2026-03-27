package com.fleetguard.rulesalerts.domain.model.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceRule {

    private UUID id;
    private String name;
    private String maintenanceType;
    private Integer intervalKm;
    private Integer warningThresholdKm;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
