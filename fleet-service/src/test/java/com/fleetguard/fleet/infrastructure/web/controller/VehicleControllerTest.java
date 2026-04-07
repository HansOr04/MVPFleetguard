package com.fleetguard.fleet.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleCommand;
import com.fleetguard.fleet.application.ports.in.RegisterVehicleUseCase.RegisterVehicleResponse;
import com.fleetguard.fleet.domain.exception.DuplicatePlateException;
import com.fleetguard.fleet.domain.exception.VehicleTypeNotFoundException;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterVehicleRequest;
import com.fleetguard.fleet.infrastructure.web.exception.GlobalExceptionHandler;
import com.fleetguard.fleet.infrastructure.web.mapper.VehicleWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleController")
class VehicleControllerTest {

    @Mock
    private RegisterVehicleUseCase registerVehicleUseCase;

    @Mock
    private VehicleWebMapper vehicleWebMapper;

    @InjectMocks
    private VehicleController vehicleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private final UUID typeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(vehicleController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    private RegisterVehicleRequest validRequest() {
        return new RegisterVehicleRequest(
                "ABC-1234", "Toyota", "Hilux", 2023,
                "Diesel", "1HGCM82633A123456", typeId);
    }

    @Nested
    @DisplayName("POST /api/vehicles")
    class RegisterVehicle {

        @Test
        @DisplayName("201 — registers vehicle successfully")
        void returns201() throws Exception {
            RegisterVehicleResponse serviceResponse = new RegisterVehicleResponse(
                    UUID.randomUUID(), "ABC-1234", "Toyota", "Hilux", 2023,
                    "Diesel", "1HGCM82633A123456", "ACTIVE", 0, "Pickup");

            when(vehicleWebMapper.toCommand(any())).thenCallRealMethod();
            when(registerVehicleUseCase.execute(any(RegisterVehicleCommand.class)))
                    .thenReturn(serviceResponse);
            when(vehicleWebMapper.toResponse(any(RegisterVehicleResponse.class))).thenCallRealMethod();

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plate").value("ABC-1234"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("409 — duplicate plate returns conflict")
        void returns409WhenDuplicatePlate() throws Exception {
            when(vehicleWebMapper.toCommand(any())).thenCallRealMethod();
            when(registerVehicleUseCase.execute(any(RegisterVehicleCommand.class)))
                    .thenThrow(new DuplicatePlateException("ABC-1234"));

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.message").value("A vehicle with plate 'ABC-1234' already exists"));
        }

        @Test
        @DisplayName("404 — unknown vehicle type returns not found")
        void returns404WhenVehicleTypeNotFound() throws Exception {
            when(vehicleWebMapper.toCommand(any())).thenCallRealMethod();
            when(registerVehicleUseCase.execute(any(RegisterVehicleCommand.class)))
                    .thenThrow(new VehicleTypeNotFoundException(typeId));

            mockMvc.perform(post("/api/vehicles")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Vehicle type not found with id: " + typeId));
        }
    }
}