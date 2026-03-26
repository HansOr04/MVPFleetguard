package com.fleetguard.fleet.domain.model.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VehicleType {
    private UUID id;
    private String name;
    private String description;
}
