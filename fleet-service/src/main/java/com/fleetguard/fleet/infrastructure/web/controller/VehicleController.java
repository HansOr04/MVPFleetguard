package com.fleetguard.fleet.infrastructure.web.controller;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageCommand;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageResponse;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase;
import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase.GetVehicleByPlateResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterMileageRequest;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterVehicleRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.MileageResponse;
import com.fleetguard.fleet.infrastructure.web.dto.response.VehicleResponse;
import com.fleetguard.fleet.infrastructure.web.mapper.MileageWebMapper;
import com.fleetguard.fleet.infrastructure.web.mapper.VehicleWebMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final RegisterVehicleUseCase registerVehicleUseCase;
    private final VehicleWebMapper vehicleWebMapper;
    private final RegisterMileageUseCase registerMileageUseCase;
    private final MileageWebMapper mileageWebMapper;
    private final GetVehicleByPlateUseCase getVehicleByPlateUseCase;

    @PostMapping
    public ResponseEntity<VehicleResponse> registerVehicle(
            @Valid @RequestBody RegisterVehicleRequest request) {
        RegisterVehicleCommand command = vehicleWebMapper.toCommand(request);
        RegisterVehicleResponse result = registerVehicleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleWebMapper.toResponse(result));
    }

    @PostMapping("/{plate}/mileage")
    public ResponseEntity<MileageResponse> registerMileage(
            @PathVariable String plate,
            @Valid @RequestBody RegisterMileageRequest request) {
        RegisterMileageCommand command = mileageWebMapper.toCommand(plate, request);
        RegisterMileageResponse result = registerMileageUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mileageWebMapper.toResponse(result));
    }

    @GetMapping("/{plate}")
    public ResponseEntity<VehicleResponse> getByPlate(@PathVariable String plate) {
        GetVehicleByPlateResponse result = getVehicleByPlateUseCase.execute(plate.toUpperCase());
        return ResponseEntity.ok(vehicleWebMapper.toResponse(result));
    }
}