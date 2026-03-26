package com.fleetguard.fleet.infrastructure.web.mapper;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageCommand;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterMileageRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.MileageResponse;
import org.springframework.stereotype.Component;

@Component
public class MileageWebMapper {

    public RegisterMileageCommand toCommand(String plate, RegisterMileageRequest request) {
        return new RegisterMileageCommand(
                plate,
                request.getMileageValue(),
                request.getRecordedBy()
        );
    }

    public MileageResponse toResponse(RegisterMileageResponse response) {
        return new MileageResponse(
                response.mileageLogId(),
                response.vehicleId(),
                response.plate(),
                response.mileageValue(),
                response.currentMileage(),
                response.recordedBy(),
                response.recordedAt(),
                response.excessiveIncrement()
        );
    }
}
