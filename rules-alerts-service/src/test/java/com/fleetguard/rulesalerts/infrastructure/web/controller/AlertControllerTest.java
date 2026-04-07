package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.in.GetAlertsByVehicleUseCase;
import com.fleetguard.rulesalerts.application.ports.in.GetAlertsUseCase;
import com.fleetguard.rulesalerts.infrastructure.web.exception.GlobalExceptionHandler;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertController")
class AlertControllerTest {

    @Mock private GetAlertsUseCase getAlertsUseCase;
    @Mock private GetAlertsByVehicleUseCase getAlertsByVehicleUseCase;

    @InjectMocks
    private AlertController controller;

    private MockMvc mockMvc;

    private GetAlertsUseCase.AlertDetail pendingDetail;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        pendingDetail = new GetAlertsUseCase.AlertDetail(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "Oil Change", "PENDING",
                LocalDateTime.now(), 5000L);
    }

    @Nested
    @DisplayName("GET /api/alerts")
    class GetAlerts {

        @Test
        @DisplayName("200 — returns all active alerts when no status param")
        void returnsAllActiveAlerts() throws Exception {
            when(getAlertsUseCase.execute(null)).thenReturn(List.of(pendingDetail));

            mockMvc.perform(get("/api/alerts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("PENDING"))
                    .andExpect(jsonPath("$[0].ruleName").value("Oil Change"));
        }

        @Test
        @DisplayName("200 — returns alerts filtered by status param")
        void returnsFilteredByStatus() throws Exception {
            when(getAlertsUseCase.execute("WARNING")).thenReturn(List.of());

            mockMvc.perform(get("/api/alerts").param("status", "WARNING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("200 — returns empty list when no active alerts")
        void returnsEmptyList() throws Exception {
            when(getAlertsUseCase.execute(null)).thenReturn(List.of());

            mockMvc.perform(get("/api/alerts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/alerts/vehicle/{plate}")
    class GetAlertsByPlate {

        @Test
        @DisplayName("200 — returns active alerts for vehicle")
        void returnsActiveAlertsForVehicle() throws Exception {
            GetAlertsByVehicleUseCase.AlertDetail detail = new GetAlertsByVehicleUseCase.AlertDetail(
                    pendingDetail.id(), pendingDetail.vehicleId(), pendingDetail.vehicleTypeId(),
                    pendingDetail.ruleId(), "Oil Change", "PENDING",
                    pendingDetail.triggeredAt(), pendingDetail.dueAtKm());

            when(getAlertsByVehicleUseCase.execute("ABC-1234")).thenReturn(List.of(detail));

            mockMvc.perform(get("/api/alerts/vehicle/ABC-1234"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("PENDING"))
                    .andExpect(jsonPath("$[0].ruleName").value("Oil Change"));
        }

        @Test
        @DisplayName("200 — returns empty list when vehicle not found")
        void returnsEmptyWhenVehicleNotFound() throws Exception {
            when(getAlertsByVehicleUseCase.execute("ZZZ-000")).thenReturn(List.of());

            mockMvc.perform(get("/api/alerts/vehicle/ZZZ-000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("200 — returns empty list when no active alerts")
        void returnsEmptyWhenNoActiveAlerts() throws Exception {
            when(getAlertsByVehicleUseCase.execute("ABC-1234")).thenReturn(List.of());

            mockMvc.perform(get("/api/alerts/vehicle/ABC-1234"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }
}