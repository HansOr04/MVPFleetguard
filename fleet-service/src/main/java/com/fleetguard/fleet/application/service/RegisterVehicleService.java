package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.exception.DuplicatePlateException;
import com.fleetguard.fleet.domain.exception.VehicleTypeNotFoundException;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterVehicleService implements RegisterVehicleUseCase {

    private final VehicleRepositoryPort vehicleRepository;

    @Override
    @Transactional
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

        return new RegisterVehicleResponse(
                saved.getId(),
                saved.getPlate().getValue(),
                saved.getBrand(),
                saved.getModel(),
                saved.getYear(),
                saved.getFuelType(),
                saved.getVin().getValue(),
                saved.getStatus().name(),
                saved.getCurrentMileage().getValue(),
                saved.getVehicleType().getName()
        );
    }
}