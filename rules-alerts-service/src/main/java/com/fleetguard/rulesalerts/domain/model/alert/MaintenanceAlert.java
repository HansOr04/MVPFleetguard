package com.fleetguard.rulesalerts.domain.model.alert;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MaintenanceAlert {

    private UUID id;
    private UUID vehicleId;
    private UUID vehicleTypeId;
    private UUID ruleId;
    private String status;
    private LocalDateTime triggeredAt;
    private Long dueAtKm;
}
