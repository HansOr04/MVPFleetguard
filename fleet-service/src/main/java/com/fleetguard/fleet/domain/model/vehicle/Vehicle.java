package com.fleetguard.fleet.domain.model.vehicle;

import com.fleetguard.fleet.domain.exception.InactiveVehicleException;
import com.fleetguard.fleet.domain.exception.InvalidMileageException;
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
            throw new IllegalArgumentException("La marca no puede ser nula o vacía");
        }
        if (model == null || model.isBlank()) {
            throw new IllegalArgumentException("El modelo no puede ser nulo o vacío");
        }
        if (fuelType == null || fuelType.isBlank()) {
            throw new IllegalArgumentException("El tipo de combustible no puede ser nulo o vacío");
        }
        if (year <= 0) {
            throw new IllegalArgumentException("El año debe ser un número entero positivo");
        }
        if (vehicleType == null) {
            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo");
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
        if (newMileage.getValue() <= 0) {
            throw new InvalidMileageException("El valor del kilometraje debe ser mayor que cero");
        }
        this.currentMileage.assertNewMileageIsNotLower(newMileage);
        this.currentMileage = newMileage;
    }
}