package com.fleetguard.rulesalerts.domain.model.association;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RuleVehicleTypeAssoc {

    private UUID id;
    private UUID ruleId;
    private UUID vehicleTypeId;
    private LocalDateTime createdAt;
}
