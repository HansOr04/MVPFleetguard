package com.fleetguard.fleet.infrastructure.web.controller;

import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase;
import com.fleetguard.fleet.application.ports.in.GetVehicleByPlateUseCase.GetVehicleByPlateResponse;
import com.fleetguard.fleet.domain.exception.VehicleNotFoundException;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleQueryController")
class VehicleQueryControllerTest {

    @Mock
    private GetVehicleByPlateUseCase getVehicleByPlateUseCase;

    @Mock
    private VehicleWebMapper vehicleWebMapper;

    @InjectMocks
    private VehicleQueryController vehicleQueryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(vehicleQueryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("GET /api/vehicles/{plate}")
    class GetByPlate {

        @Test
        @DisplayName("200 — returns vehicle data when plate exists")
        void returns200() throws Exception {
            GetVehicleByPlateResponse serviceResponse = new GetVehicleByPlateResponse(
                    UUID.randomUUID(), "ABC-1234", "Toyota", "Hilux",
                    2023, "Diesel", "1HGCM82633A123456",
                    "ACTIVE", 1000L, "Pickup");

            when(getVehicleByPlateUseCase.execute("ABC-1234")).thenReturn(serviceResponse);
            when(vehicleWebMapper.toResponse(any(GetVehicleByPlateResponse.class))).thenCallRealMethod();

            mockMvc.perform(get("/api/vehicles/ABC-1234"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.plate").value("ABC-1234"))
                    .andExpect(jsonPath("$.brand").value("Toyota"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"))
                    .andExpect(jsonPath("$.currentMileage").value(1000));
        }

        @Test
        @DisplayName("404 — plate not found returns not found")
        void returns404WhenPlateNotFound() throws Exception {
            when(getVehicleByPlateUseCase.execute("ZZZ-9999"))
                    .thenThrow(new VehicleNotFoundException("ZZZ-9999"));

            mockMvc.perform(get("/api/vehicles/ZZZ-9999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("Vehículo no encontrado con la placa: ZZZ-9999"));
        }
    }
}