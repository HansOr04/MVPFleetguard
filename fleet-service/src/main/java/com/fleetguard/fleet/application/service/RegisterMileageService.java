package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase;
import com.fleetguard.fleet.application.ports.out.EventPublisherPort;
import com.fleetguard.fleet.application.ports.out.MileageLogRepositoryPort;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.event.DomainEvent;
import com.fleetguard.fleet.domain.exception.VehicleNotFoundException;
import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public RegisterMileageResponse execute(RegisterMileageCommand command) {

        Vehicle vehicle = vehicleRepository.findByPlate(command.plate())
                .orElseThrow(() -> new VehicleNotFoundException(command.plate()));

        Mileage previousMileage = vehicle.getCurrentMileage();
        Mileage newMileage = new Mileage(command.mileageValue());
        boolean excessiveIncrement = newMileage.isExcessiveIncrement(previousMileage);

        vehicle.updateMileage(newMileage);

        LocalDateTime recordedAt = LocalDateTime.now();
        MileageLog mileageLog = MileageLog.create(
                vehicle.getId(),
                vehicle.getVehicleType().getId(),
                vehicle.getStatus().name(),
                previousMileage,
                newMileage,
                recordedAt,
                command.recordedBy(),
                excessiveIncrement
        );

        vehicleRepository.save(vehicle);
        MileageLog savedLog = mileageLogRepository.save(mileageLog);

        List<DomainEvent> events = mileageLog.pullDomainEvents();
        log.info("Events to publish: {}", events.size());
        for (DomainEvent event : events) {
            try {
                eventPublisher.publish(event);
                log.info("Event published: {}", event.getClass().getSimpleName());
            } catch (Exception e) {
                log.error("Failed to publish domain event: {}", event.getClass().getSimpleName(), e);
            }
        }

        return new RegisterMileageResponse(
                savedLog.getId(),
                vehicle.getId(),
                vehicle.getPlate().getValue(),
                savedLog.getPreviousMileage().getValue(),
                savedLog.getMileageValue().getValue(),
                savedLog.getKmTraveled(),
                vehicle.getCurrentMileage().getValue(),
                savedLog.getRecordedBy(),
                savedLog.getRecordedAt(),
                excessiveIncrement,
                null
        );
    }
}