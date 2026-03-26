package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase;
import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import com.fleetguard.fleet.application.ports.out.MileageLogRepositoryPort;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.event.DomainEvent;
import com.fleetguard.fleet.domain.exception.InvalidMileageException;
import com.fleetguard.fleet.domain.exception.VehicleNotFoundException;
import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterMileageService implements RegisterMileageUseCase {

    private final VehicleRepositoryPort vehicleRepository;
    private final MileageLogRepositoryPort mileageLogRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    public RegisterMileageResponse execute(RegisterMileageCommand command) {

        Vehicle vehicle = vehicleRepository.findByPlate(command.plate())
                .orElseThrow(() -> new VehicleNotFoundException(command.plate()));

        if (command.mileageValue() <= 0) {
            throw new InvalidMileageException("Mileage value must be greater than zero");
        }

        Mileage newMileage = new Mileage(command.mileageValue());

        boolean excessiveIncrement = newMileage.isExcessiveIncrement(vehicle.getCurrentMileage());

        vehicle.updateMileage(newMileage);

        LocalDateTime recordedAt = LocalDateTime.now();
        MileageLog mileageLog = MileageLog.create(
                vehicle.getId(),
                vehicle.getVehicleType().getId(),
                vehicle.getStatus().name(),
                newMileage,
                recordedAt,
                command.recordedBy(),
                excessiveIncrement
        );

        vehicleRepository.save(vehicle);
        MileageLog savedLog = mileageLogRepository.save(mileageLog);

        List<DomainEvent> events = mileageLog.pullDomainEvents();
        for (DomainEvent event : events) {
            try {
                eventPublisher.publish(event);
            } catch (Exception e) {
                log.error("Failed to publish domain event: {}", event.getClass().getSimpleName(), e);
            }
        }

        return new RegisterMileageResponse(
                savedLog.getId(),
                vehicle.getId(),
                vehicle.getPlate().getValue(),
                savedLog.getMileageValue().getValue(),
                vehicle.getCurrentMileage().getValue(),
                savedLog.getRecordedBy(),
                savedLog.getRecordedAt(),
                excessiveIncrement
        );
    }
}
