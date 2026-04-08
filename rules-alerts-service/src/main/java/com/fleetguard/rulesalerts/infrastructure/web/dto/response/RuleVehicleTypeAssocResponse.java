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
public class RuleVehicleTypeAssocResponse {

    private UUID id;

    private UUID ruleId;

    private UUID vehicleTypeId;

    private LocalDateTime createdAt;
}