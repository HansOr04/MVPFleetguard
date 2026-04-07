package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.application.ports.out.VehicleQueryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
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
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertController")
class AlertControllerTest {

    @Mock private MaintenanceAlertRepositoryPort maintenanceAlertRepositoryPort;
    @Mock private VehicleQueryPort vehicleQueryPort;
    @Mock private MaintenanceRuleQueryPort maintenanceRuleQueryPort;

    @InjectMocks
    private AlertController controller;

    private MockMvc mockMvc;

    private UUID vehicleId;
    private UUID ruleId;
    private MaintenanceAlert pendingAlert;
    private MaintenanceRule rule;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        vehicleId = UUID.randomUUID();
        ruleId = UUID.randomUUID();

        pendingAlert = new MaintenanceAlert(
                UUID.randomUUID(), vehicleId, UUID.randomUUID(), ruleId,
                "PENDING", LocalDateTime.now(), 5000L);

        rule = new MaintenanceRule(
                ruleId, "Oil Change", "OIL",
                5000, 500, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("GET /api/alerts")
    class GetAlerts {

        @Test
        @DisplayName("200 — returns all active alerts when no status param")
        void returnsAllActiveAlerts() throws Exception {
            when(maintenanceAlertRepositoryPort.findAllActive()).thenReturn(List.of(pendingAlert));
            when(maintenanceRuleQueryPort.findById(ruleId)).thenReturn(Optional.of(rule));

            mockMvc.perform(get("/api/alerts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("PENDING"))
                    .andExpect(jsonPath("$[0].ruleName").value("Oil Change"));
        }

        @Test
        @DisplayName("200 — returns alerts filtered by status param")
        void returnsFilteredByStatus() throws Exception {
            when(maintenanceAlertRepositoryPort.findByStatus("WARNING")).thenReturn(List.of());

            mockMvc.perform(get("/api/alerts").param("status", "WARNING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("200 — returns empty list when no active alerts")
        void returnsEmptyListWhenNoAlerts() throws Exception {
            when(maintenanceAlertRepositoryPort.findAllActive()).thenReturn(List.of());

            mockMvc.perform(get("/api/alerts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("200 — uses fallback rule name when rule not found")
        void usesFallbackRuleName() throws Exception {
            when(maintenanceAlertRepositoryPort.findAllActive()).thenReturn(List.of(pendingAlert));
            when(maintenanceRuleQueryPort.findById(ruleId)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/alerts"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].ruleName").value("Regla desconocida"));
        }
    }

    @Nested
    @DisplayName("GET /api/alerts/vehicle/{plate}")
    class GetAlertsByPlate {

        @Test
        @DisplayName("200 — returns active alerts for vehicle")
        void returnsActiveAlertsForVehicle() throws Exception {
            when(vehicleQueryPort.findVehicleIdByPlate("ABC-1234"))
                    .thenReturn(Optional.of(vehicleId));
            when(maintenanceAlertRepositoryPort.findActiveByVehicleId(vehicleId))
                    .thenReturn(List.of(pendingAlert));
            when(maintenanceRuleQueryPort.findById(ruleId)).thenReturn(Optional.of(rule));

            mockMvc.perform(get("/api/alerts/vehicle/ABC-1234"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].status").value("PENDING"))
                    .andExpect(jsonPath("$[0].ruleName").value("Oil Change"));
        }

        @Test
        @DisplayName("200 — returns empty list when vehicle not found in fleet-service")
        void returnsEmptyWhenVehicleNotFound() throws Exception {
            when(vehicleQueryPort.findVehicleIdByPlate("ZZZ-000"))
                    .thenReturn(Optional.empty());

            mockMvc.perform(get("/api/alerts/vehicle/ZZZ-000"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("200 — returns empty list when vehicle has no active alerts")
        void returnsEmptyWhenNoActiveAlerts() throws Exception {
            when(vehicleQueryPort.findVehicleIdByPlate("ABC-1234"))
                    .thenReturn(Optional.of(vehicleId));
            when(maintenanceAlertRepositoryPort.findActiveByVehicleId(vehicleId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/alerts/vehicle/ABC-1234"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("200 — normalizes plate to uppercase before querying")
        void normalizesPlateToUppercase() throws Exception {
            when(vehicleQueryPort.findVehicleIdByPlate("ABC-1234"))
                    .thenReturn(Optional.of(vehicleId));
            when(maintenanceAlertRepositoryPort.findActiveByVehicleId(vehicleId))
                    .thenReturn(List.of());

            mockMvc.perform(get("/api/alerts/vehicle/abc-1234"))
                    .andExpect(status().isOk());
        }
    }
}