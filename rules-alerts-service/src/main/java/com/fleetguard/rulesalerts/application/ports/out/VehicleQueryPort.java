package com.fleetguard.rulesalerts.application.ports.out;

import java.util.Optional;
import java.util.UUID;

public interface VehicleQueryPort {
    Optional<UUID> findVehicleIdByPlate(String plate);
}