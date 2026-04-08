package com.fleetguard.fleet.infrastructure.persistence.adapter;

import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.mapper.VehicleMapper;
import com.fleetguard.fleet.infrastructure.persistence.repository.VehicleJpaRepository;
import com.fleetguard.fleet.infrastructure.persistence.repository.VehicleTypeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class VehicleRepositoryAdapter implements VehicleRepositoryPort {

    private final VehicleJpaRepository vehicleRepository;
    private final VehicleTypeJpaRepository vehicleTypeRepository;

    @Override
    public Vehicle save(Vehicle vehicle) {
        VehicleJpaEntity entity = VehicleMapper.toJpaEntity(vehicle);
        return VehicleMapper.toDomain(vehicleRepository.save(entity));
    }

    @Override
    public Optional<Vehicle> findByPlate(String plate) {
        return vehicleRepository.findByPlate(plate)
                .map(VehicleMapper::toDomain);
    }

    @Override
    public boolean existsByPlate(String plate) {
        return vehicleRepository.existsByPlate(plate);
    }

    @Override
    public Optional<VehicleType> findVehicleTypeById(UUID id) {
        return vehicleTypeRepository.findById(id)
                .map(VehicleMapper::toDomain);
    }

    @Override
    public Optional<Vehicle> findById(UUID id) {
        return vehicleRepository.findById(id)
                .map(VehicleMapper::toDomain);
    }
}