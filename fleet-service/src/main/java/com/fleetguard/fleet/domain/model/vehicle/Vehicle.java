package com.fleetguard.fleet.domain.model.vehicle;

import com.fleetguard.fleet.domain.exception.InactiveVehicleException;
import com.fleetguard.fleet.domain.model.AggregateRoot;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle extends AggregateRoot {

    private UUID id;
    private Plate plate;
    private String brand;
    private String model;
    private int year;
    private String fuelType;
    private Vin vin;
    private VehicleStatus status;
    private Mileage currentMileage;
    private VehicleType vehicleType;

    public static Vehicle create(Plate plate, String brand, String model,
                                 int year, String fuelType, Vin vin, VehicleType vehicleType) {
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Brand cannot be null or empty");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("Model cannot be null or empty");
        }
        if (fuelType == null || fuelType.isBlank()) {
            throw new IllegalArgumentException("Fuel type cannot be null or empty");
        }
        if (year <= 0) {
            throw new IllegalArgumentException("Year must be a positive integer");
        }
        if (vehicleType == null) {
            throw new IllegalArgumentException("VehicleType cannot be null");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.id = UUID.randomUUID();
        vehicle.plate = plate;
        vehicle.brand = brand;
        vehicle.model = model;
        vehicle.year = year;
        vehicle.fuelType = fuelType;
        vehicle.vin = vin;
        vehicle.status = VehicleStatus.ACTIVE;
        vehicle.currentMileage = Mileage.zero();
        vehicle.vehicleType = vehicleType;
        return vehicle;
    }

    public void updateMileage(Mileage newMileage) {
        if (this.status != VehicleStatus.ACTIVE) {
            throw new InactiveVehicleException(this.id);
        }
        this.currentMileage.validateNotLessThan(newMileage);
        this.currentMileage = newMileage;
    }
}
