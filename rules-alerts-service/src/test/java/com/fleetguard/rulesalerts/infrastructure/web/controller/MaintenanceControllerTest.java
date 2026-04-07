package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase;
import com.fleetguard.rulesalerts.domain.exception.AlertNotFoundException;
import com.fleetguard.rulesalerts.domain.exception.InvalidMaintenanceException;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.RegisterMaintenanceRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRecordResponse;
import com.fleetguard.rulesalerts.infrastructure.web.exception.GlobalExceptionHandler;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.MaintenanceWebMapper;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MaintenanceController")
class MaintenanceControllerTest {

    @Mock private RegisterMaintenanceUseCase registerMaintenanceUseCase;
    @Mock private MaintenanceWebMapper maintenanceWebMapper;

    @InjectMocks
    private MaintenanceController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    private RegisterMaintenanceRequest validRequest() {
        return RegisterMaintenanceRequest.builder()
                .alertId(UUID.randomUUID())
                .serviceType("Oil Change")
                .description("Routine")
                .cost(new BigDecimal("50.00"))
                .provider("AutoShop")
                .performedAt(LocalDateTime.now().minusDays(1))
                .mileageAtService(45000L)
                .recordedBy("Juan")
                .build();
    }

    @Nested
    @DisplayName("POST /api/maintenance/{plate}")
    class RegisterMaintenance {

        @Test
        @DisplayName("201 — registers maintenance successfully")
        void returns201() throws Exception {
            MaintenanceRecordResponse response = MaintenanceRecordResponse.builder()
                    .id(UUID.randomUUID())
                    .vehicleId(UUID.randomUUID())
                    .plate("ABC-1234")
                    .serviceType("Oil Change")
                    .mileageAtService(45000L)
                    .build();

            when(maintenanceWebMapper.toCommand(any(), any())).thenReturn(
                    new RegisterMaintenanceUseCase.RegisterMaintenanceCommand(
                            "ABC-1234", UUID.randomUUID(), "Oil Change",
                            "Routine", new BigDecimal("50.00"), "AutoShop",
                            LocalDateTime.now().minusDays(1), 45000L, "Juan"));
            when(registerMaintenanceUseCase.execute(any())).thenReturn(
                    new RegisterMaintenanceUseCase.RegisterMaintenanceResponse(
                            response.getId(), response.getVehicleId(), "ABC-1234",
                            UUID.randomUUID(), UUID.randomUUID(), "Oil Change",
                            "Routine", new BigDecimal("50.00"), "AutoShop",
                            LocalDateTime.now().minusDays(1), 45000L, "Juan",
                            LocalDateTime.now()));
            when(maintenanceWebMapper.toResponse(any())).thenReturn(response);

            mockMvc.perform(post("/api/maintenance/ABC-1234")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.plate").value("ABC-1234"))
                    .andExpect(jsonPath("$.serviceType").value("Oil Change"));
        }

        @Test
        @DisplayName("400 — rejects missing serviceType")
        void rejects400WhenServiceTypeMissing() throws Exception {
            RegisterMaintenanceRequest request = RegisterMaintenanceRequest.builder()
                    .alertId(UUID.randomUUID())
                    .performedAt(LocalDateTime.now().minusDays(1))
                    .mileageAtService(45000L)
                    .recordedBy("Juan")
                    .build();

            mockMvc.perform(post("/api/maintenance/ABC-1234")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation failed"));
        }

        @Test
        @DisplayName("400 — rejects mileageAtService of zero — boundary")
        void rejects400WhenMileageZero() throws Exception {
            RegisterMaintenanceRequest request = RegisterMaintenanceRequest.builder()
                    .alertId(UUID.randomUUID())
                    .serviceType("Oil Change")
                    .performedAt(LocalDateTime.now().minusDays(1))
                    .mileageAtService(0L)
                    .recordedBy("Juan")
                    .build();

            mockMvc.perform(post("/api/maintenance/ABC-1234")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation failed"));
        }

        @Test
        @DisplayName("400 — InvalidMaintenanceException from service")
        void returns400WhenServiceThrows() throws Exception {
            when(maintenanceWebMapper.toCommand(any(), any())).thenReturn(
                    new RegisterMaintenanceUseCase.RegisterMaintenanceCommand(
                            "ABC-1234", UUID.randomUUID(), "Oil Change",
                            null, null, null,
                            LocalDateTime.now().plusDays(1), 45000L, "Juan"));
            when(registerMaintenanceUseCase.execute(any()))
                    .thenThrow(new InvalidMaintenanceException("La fecha del servicio no puede ser futura"));

            mockMvc.perform(post("/api/maintenance/ABC-1234")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("La fecha del servicio no puede ser futura"));
        }

        @Test
        @DisplayName("404 — alert not found")
        void returns404WhenAlertNotFound() throws Exception {
            UUID alertId = UUID.randomUUID();
            when(maintenanceWebMapper.toCommand(any(), any())).thenReturn(
                    new RegisterMaintenanceUseCase.RegisterMaintenanceCommand(
                            "ABC-1234", alertId, "Oil Change",
                            null, null, null,
                            LocalDateTime.now().minusDays(1), 45000L, "Juan"));
            when(registerMaintenanceUseCase.execute(any()))
                    .thenThrow(new AlertNotFoundException(alertId));

            mockMvc.perform(post("/api/maintenance/ABC-1234")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validRequest())))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}