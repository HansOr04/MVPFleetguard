package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.exception.VehicleNotFoundException;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetVehicleByPlateService implements GetVehicleByPlateUseCase {

    private final VehicleRepositoryPort vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    public GetVehicleByPlateResponse execute(String plate) {
        Vehicle vehicle = vehicleRepository.findByPlate(plate)
                .orElseThrow(() -> new VehicleNotFoundException(plate));

        return new GetVehicleByPlateResponse(
                vehicle.getId(),
                vehicle.getPlate().getValue(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getYear(),
                vehicle.getFuelType(),
                vehicle.getVin().getValue(),
                vehicle.getStatus().name(),
                vehicle.getCurrentMileage().getValue(),
                vehicle.getVehicleType().getName()
        );
    }
}