package com.fleetguard.fleet.infrastructure.web.controller;

import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase;
import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase.GetVehicleByPlateResponse;
import com.fleetguard.fleet.infrastructure.web.dto.response.VehicleResponse;
import com.fleetguard.fleet.infrastructure.web.mapper.VehicleWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class VehicleQueryController {

    private final GetVehicleByPlateUseCase getVehicleByPlateUseCase;
    private final VehicleWebMapper vehicleWebMapper;

    @GetMapping("/{plate}")
    public ResponseEntity<VehicleResponse> getByPlate(@PathVariable String plate) {
        GetVehicleByPlateResponse result = getVehicleByPlateUseCase.execute(plate.toUpperCase());
        return ResponseEntity.ok(vehicleWebMapper.toResponse(result));
    }
}