package com.fleetguard.fleet.domain.model.vehicle;

import com.fleetguard.fleet.domain.exception.InactiveVehicleException;
import com.fleetguard.fleet.domain.model.AggregateRoot;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;

import java.util.UUID;

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

    private Vehicle() {
    }

    public static Vehicle create(Plate plate, String brand, String model,
                                 int year, String fuelType, Vin vin, VehicleType vehicleType) {
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

    public UUID getId() { return id; }

    public Plate getPlate() { return plate; }

    public String getBrand() { return brand; }

    public String getModel() { return model; }

    public int getYear() { return year; }

    public String getFuelType() { return fuelType; }

    public Vin getVin() { return vin; }

    public VehicleStatus getStatus() { return status; }

    public Mileage getCurrentMileage() { return currentMileage; }

    public VehicleType getVehicleType() { return vehicleType; }
}
