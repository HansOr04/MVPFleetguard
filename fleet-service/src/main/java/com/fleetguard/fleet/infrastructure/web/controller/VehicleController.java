package com.fleetguard.fleet.infrastructure.web.controller;

import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterVehicleRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.VehicleResponse;
import com.fleetguard.fleet.infrastructure.web.mapper.WebMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final RegisterVehicleUseCase registerVehicleUseCase;
    private final WebMapper mapper;

    public VehicleController(RegisterVehicleUseCase registerVehicleUseCase, WebMapper mapper) {
        this.registerVehicleUseCase = registerVehicleUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> registerVehicle(
            @Valid @RequestBody RegisterVehicleRequest request) {

        RegisterVehicleCommand command = mapper.toCommand(request);
        RegisterVehicleResponse result = registerVehicleUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapper.toResponse(result));
    }
}
