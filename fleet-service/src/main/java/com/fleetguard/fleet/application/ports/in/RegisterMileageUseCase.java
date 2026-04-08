package com.fleetguard.fleet.application.ports.in;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RegisterMileageUseCase {

    RegisterMileageResponse execute(RegisterMileageCommand command);

    record RegisterMileageCommand(
            String plate,
            long mileageValue,
            String recordedBy
    ) {
    }

    record RegisterMileageResponse(
            UUID mileageLogId,
            UUID vehicleId,
            String plate,
            long previousMileage,
            long mileageValue,
            long kmTraveled,
            long currentMileage,
            String recordedBy,
            LocalDateTime recordedAt,
            boolean excessiveIncrement,
            UUID alertId
    ) {
    }
}