package com.fleetguard.fleet.domain.model.vehicle;

import java.util.UUID;

public class VehicleType {

    private UUID id;
    private String name;

    public VehicleType(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
