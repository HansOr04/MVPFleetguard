package com.fleetguard.fleet.application.ports.out;

import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;

import java.util.Optional;
import java.util.UUID;

public interface VehicleRepositoryPort {

    Vehicle save(Vehicle vehicle);

    Optional<Vehicle> findByPlate(String plate);

    boolean existsByPlate(String plate);

    Optional<VehicleType> findVehicleTypeById(UUID id);
}
