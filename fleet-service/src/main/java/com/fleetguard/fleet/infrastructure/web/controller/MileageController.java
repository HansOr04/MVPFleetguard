package com.fleetguard.fleet.infrastructure.web.controller;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageCommand;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterMileageRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.MileageResponse;
import com.fleetguard.fleet.infrastructure.web.mapper.MileageWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class MileageController {

    private final RegisterMileageUseCase registerMileageUseCase;
    private final MileageWebMapper mileageWebMapper;

    @PostMapping("/{plate}/mileage")
    public ResponseEntity<MileageResponse> registerMileage(
            @PathVariable String plate,
            @Valid @RequestBody RegisterMileageRequest request) {
        RegisterMileageCommand command = mileageWebMapper.toCommand(plate, request);
        RegisterMileageResponse result = registerMileageUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mileageWebMapper.toResponse(result));
    }

}