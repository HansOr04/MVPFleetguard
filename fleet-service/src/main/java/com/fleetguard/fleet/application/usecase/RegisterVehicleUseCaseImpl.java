package com.fleetguard.fleet.application.usecase;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.exception.DuplicatePlateException;
import com.fleetguard.fleet.domain.exception.VehicleTypeNotFoundException;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;

public class RegisterVehicleUseCaseImpl implements RegisterVehicleUseCase {

    private final VehicleRepositoryPort vehicleRepository;

    public RegisterVehicleUseCaseImpl(VehicleRepositoryPort vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public RegisterVehicleResponse execute(RegisterVehicleCommand command) {

        if (vehicleRepository.existsByPlate(command.plate())) {
            throw new DuplicatePlateException(command.plate());
        }

        Plate plate = new Plate(command.plate());
        Vin vin = new Vin(command.vin());

        VehicleType type = vehicleRepository.findVehicleTypeById(command.vehicleTypeId())
                .orElseThrow(() -> new VehicleTypeNotFoundException(command.vehicleTypeId()));

        Vehicle vehicle = Vehicle.create(plate, command.brand(), command.model(),
                command.year(), command.fuelType(), vin, type);

        Vehicle saved = vehicleRepository.save(vehicle);

        return new RegisterVehicleResponse(saved.getId(), saved.getStatus().name());
    }
}
