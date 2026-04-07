package com.fleetguard.fleet.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageCommand;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageResponse;
import com.fleetguard.fleet.domain.exception.InactiveVehicleException;
import com.fleetguard.fleet.domain.exception.InvalidMileageException;
import com.fleetguard.fleet.domain.exception.VehicleNotFoundException;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterMileageRequest;
import com.fleetguard.fleet.infrastructure.web.exception.GlobalExceptionHandler;
import com.fleetguard.fleet.infrastructure.web.mapper.MileageWebMapper;
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

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MileageController")
class MileageControllerTest {

    @Mock
    private RegisterMileageUseCase registerMileageUseCase;

    @Mock
    private MileageWebMapper mileageWebMapper;

    @InjectMocks
    private MileageController mileageController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private final UUID vehicleId = UUID.randomUUID();
    private final UUID mileageLogId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(mileageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private RegisterMileageRequest validRequest() {
        return new RegisterMileageRequest(1000L, "driver@fleetguard.com");
    }

    private RegisterMileageResponse validServiceResponse() {
        return new RegisterMileageResponse(
                mileageLogId, vehicleId, "ABC-1234",
                0L, 1000L, 1000L, 1000L,
                "driver@fleetguard.com", LocalDateTime.now(),
                false, null);
    }

    @Nested
    @DisplayName("POST /api/vehicles/{plate}/mileage")
    class RegisterMileage {

        @Test
        @DisplayName("201 — registers mileage successfully")
        void returns201() throws Exception {
            when(mileageWebMapper.toCommand(any(), any())).thenCallRealMethod();
            when(registerMileageUseCase.execute(any(RegisterMileageCommand.class)))
                    .thenReturn(validServiceResponse());
            when(mileageWebMapper.toResponse(any(RegisterMileageResponse.class))).thenCallRealMethod();

            mockMvc.perform(post("/api/vehicles/ABC-1234/mileage")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plate").value("ABC-1234"))
                    .andExpect(jsonPath("$.mileageValue").value(1000))
                    .andExpect(jsonPath("$.excessiveIncrement").value(false));
        }

        @Test
        @DisplayName("400 — invalid mileage value")
        void returns400WhenInvalidMileage() throws Exception {
            when(mileageWebMapper.toCommand(any(), any())).thenCallRealMethod();
            when(registerMileageUseCase.execute(any(RegisterMileageCommand.class)))
                    .thenThrow(new InvalidMileageException("Mileage value must be greater than zero"));

            mockMvc.perform(post("/api/vehicles/ABC-1234/mileage")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Mileage value must be greater than zero"));
        }

        @Test
        @DisplayName("400 — inactive vehicle")
        void returns400WhenVehicleInactive() throws Exception {
            when(mileageWebMapper.toCommand(any(), any())).thenCallRealMethod();
            when(registerMileageUseCase.execute(any(RegisterMileageCommand.class)))
                    .thenThrow(new InactiveVehicleException(vehicleId));

            mockMvc.perform(post("/api/vehicles/ABC-1234/mileage")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("Vehicle " + vehicleId + " is not active"));
        }

        @Test
        @DisplayName("404 — vehicle not found")
        void returns404WhenVehicleNotFound() throws Exception {
            when(mileageWebMapper.toCommand(any(), any())).thenCallRealMethod();
            when(registerMileageUseCase.execute(any(RegisterMileageCommand.class)))
                    .thenThrow(new VehicleNotFoundException("ABC-1234"));

            mockMvc.perform(post("/api/vehicles/ABC-1234/mileage")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Vehicle not found with plate: ABC-1234"));
        }
    }
}