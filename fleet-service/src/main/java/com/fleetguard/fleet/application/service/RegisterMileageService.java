package com.fleetguard.fleet.application.service;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase;
import com.fleetguard.fleet.application.ports.out.MileageLogRepositoryPort;
import com.fleetguard.fleet.application.ports.out.VehicleRepositoryPort;
import com.fleetguard.fleet.domain.event.DomainEvent;
import com.fleetguard.fleet.domain.exception.VehicleNotFoundException;
import com.fleetguard.fleet.domain.factory.MileageLogFactory;
import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MileageLogFactory mileageLogFactory;

    @Override
    @Transactional
    public RegisterMileageResponse execute(RegisterMileageCommand command) {

        Vehicle vehicle = vehicleRepository.findByPlate(command.plate())
                .orElseThrow(() -> new VehicleNotFoundException(command.plate()));

        Mileage newMileage = new Mileage(command.mileageValue());
        vehicle.updateMileage(newMileage);

        MileageLog mileageLog = mileageLogFactory.create(
                vehicle,
                command.mileageValue(),
                command.recordedBy(),
                LocalDateTime.now()
        );

        vehicleRepository.save(vehicle);
        MileageLog savedLog = mileageLogRepository.save(mileageLog);

        List<DomainEvent> events = mileageLog.pullDomainEvents();
        events.forEach(event -> {
            log.info("Scheduling event for post-commit publish: {}",
                    event.getClass().getSimpleName());
            applicationEventPublisher.publishEvent(event);
        });

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
                savedLog.isExcessiveIncrement(),
                null
        );
    }
}