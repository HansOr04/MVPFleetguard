package com.fleetguard.fleet.infrastructure.persistence.mapper;

import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleStatus;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleTypeJpaEntity;

public class VehicleMapper {
    public static VehicleJpaEntity toJpaEntity(Vehicle vehicle) {
        return new VehicleJpaEntity(
                vehicle.getId(),
                vehicle.getPlate().getValue(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getFuelType(),
                vehicle.getVin().getValue(),
                vehicle.getStatus().name(),
                vehicle.getCurrentMileage().getValue(),
                new VehicleTypeJpaEntity(
                        vehicle.getVehicleType().getId(),
                        vehicle.getVehicleType().getName(),
                        vehicle.getVehicleType().getDescription()
                )
        );
    }

    public static Vehicle toDomain(VehicleJpaEntity entity) {
        return new Vehicle(
                entity.getId(),
                new Plate(entity.getPlate()),
                entity.getBrand(),
                entity.getModel(),
                entity.getYear(),
                entity.getFuelType(),
                new Vin(entity.getVin()),
                VehicleStatus.valueOf(entity.getStatus()),
                new Mileage(entity.getCurrentMileage()),
                toDomain(entity.getVehicleType())
        );
    }

    public static VehicleType toDomain(VehicleTypeJpaEntity entity) {
        return new VehicleType(
                entity.getId(),
                entity.getName(),
                entity.getDescription()
        );
    }

    public static VehicleTypeJpaEntity toJpaEntity(VehicleType vehicleType) {
        return new VehicleTypeJpaEntity(
                vehicleType.getId(),
                vehicleType.getName(),
                vehicleType.getDescription()
        );
    }
}