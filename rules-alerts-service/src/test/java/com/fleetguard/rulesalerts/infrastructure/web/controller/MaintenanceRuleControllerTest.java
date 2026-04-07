package com.fleetguard.rulesalerts.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase;
import com.fleetguard.rulesalerts.domain.exception.DuplicateAssociationException;
import com.fleetguard.rulesalerts.domain.exception.MaintenanceRuleNotFoundException;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.AssociateVehicleTypeRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.exception.GlobalExceptionHandler;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.AssociateVehicleTypeWebMapper;
import com.fleetguard.rulesalerts.infrastructure.web.mapper.MaintenanceRuleWebMapper;
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
@DisplayName("MaintenanceRuleController")
class MaintenanceRuleControllerTest {

    @Mock private CreateMaintenanceRuleUseCase createMaintenanceRuleUseCase;
    @Mock private MaintenanceRuleWebMapper maintenanceRuleWebMapper;
    @Mock private AssociateVehicleTypeUseCase associateVehicleTypeUseCase;
    @Mock private AssociateVehicleTypeWebMapper associateVehicleTypeWebMapper;

    @InjectMocks
    private MaintenanceRuleController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /api/maintenance-rules")
    class CreateRule {

        @Test
        @DisplayName("201 — creates rule successfully")
        void creates201() throws Exception {
            CreateMaintenanceRuleRequest request = CreateMaintenanceRuleRequest.builder()
                    .name("Oil Change")
                    .maintenanceType("OIL")
                    .intervalKm(5000)
                    .warningThresholdKm(500)
                    .build();

            CreateMaintenanceRuleUseCase.CreateMaintenanceRuleResponse response =
                    new CreateMaintenanceRuleUseCase.CreateMaintenanceRuleResponse(
                            UUID.randomUUID(), "Oil Change", "OIL",
                            5000, 500, "ACTIVE",
                            LocalDateTime.now(), LocalDateTime.now());

            when(maintenanceRuleWebMapper.toCommand(any())).thenReturn(
                    new CreateMaintenanceRuleUseCase.CreateMaintenanceRuleCommand(
                            "Oil Change", "OIL", 5000, 500));
            when(createMaintenanceRuleUseCase.execute(any())).thenReturn(response);
            when(maintenanceRuleWebMapper.toResponse(any())).thenReturn(
                    com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse
                            .builder()
                            .id(response.id())
                            .name(response.name())
                            .status(response.status())
                            .build());

            mockMvc.perform(post("/api/maintenance-rules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("Oil Change"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("400 — rejects missing name")
        void rejects400WhenNameMissing() throws Exception {
            CreateMaintenanceRuleRequest request = CreateMaintenanceRuleRequest.builder()
                    .maintenanceType("OIL")
                    .intervalKm(5000)
                    .build();

            mockMvc.perform(post("/api/maintenance-rules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation failed"));
        }

        @Test
        @DisplayName("400 — rejects missing maintenanceType")
        void rejects400WhenTypeMissing() throws Exception {
            CreateMaintenanceRuleRequest request = CreateMaintenanceRuleRequest.builder()
                    .name("Oil Change")
                    .intervalKm(5000)
                    .build();

            mockMvc.perform(post("/api/maintenance-rules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation failed"));
        }

        @Test
        @DisplayName("400 — rejects intervalKm of zero — boundary")
        void rejects400WhenIntervalKmZero() throws Exception {
            CreateMaintenanceRuleRequest request = CreateMaintenanceRuleRequest.builder()
                    .name("Oil Change")
                    .maintenanceType("OIL")
                    .intervalKm(0)
                    .build();

            mockMvc.perform(post("/api/maintenance-rules")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation failed"));
        }
    }

    @Nested
    @DisplayName("POST /api/maintenance-rules/{id}/vehicle-types")
    class AssociateVehicleType {

        @Test
        @DisplayName("201 — associates vehicle type successfully")
        void associates201() throws Exception {
            UUID ruleId = UUID.randomUUID();
            UUID vehicleTypeId = UUID.randomUUID();

            AssociateVehicleTypeRequest request = AssociateVehicleTypeRequest.builder()
                    .vehicleTypeId(vehicleTypeId)
                    .build();

            AssociateVehicleTypeUseCase.AssociateVehicleTypeResponse response =
                    new AssociateVehicleTypeUseCase.AssociateVehicleTypeResponse(
                            UUID.randomUUID(), ruleId, vehicleTypeId, LocalDateTime.now());

            when(associateVehicleTypeWebMapper.toCommand(any(), any())).thenReturn(
                    new AssociateVehicleTypeUseCase.AssociateVehicleTypeCommand(ruleId, vehicleTypeId));
            when(associateVehicleTypeUseCase.execute(any())).thenReturn(response);
            when(associateVehicleTypeWebMapper.toResponse(any())).thenReturn(
                    com.fleetguard.rulesalerts.infrastructure.web.dto.response.RuleVehicleTypeAssocResponse
                            .builder()
                            .id(response.id())
                            .ruleId(ruleId)
                            .vehicleTypeId(vehicleTypeId)
                            .build());

            mockMvc.perform(post("/api/maintenance-rules/{id}/vehicle-types", ruleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.ruleId").value(ruleId.toString()))
                    .andExpect(jsonPath("$.vehicleTypeId").value(vehicleTypeId.toString()));
        }

        @Test
        @DisplayName("400 — rejects missing vehicleTypeId")
        void rejects400WhenVehicleTypeIdMissing() throws Exception {
            UUID ruleId = UUID.randomUUID();

            mockMvc.perform(post("/api/maintenance-rules/{id}/vehicle-types", ruleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"vehicleTypeId\": null}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Validation failed"));
        }

        @Test
        @DisplayName("404 — rule not found")
        void returns404WhenRuleNotFound() throws Exception {
            UUID ruleId = UUID.randomUUID();
            UUID vehicleTypeId = UUID.randomUUID();

            AssociateVehicleTypeRequest request = AssociateVehicleTypeRequest.builder()
                    .vehicleTypeId(vehicleTypeId)
                    .build();

            when(associateVehicleTypeWebMapper.toCommand(any(), any())).thenReturn(
                    new AssociateVehicleTypeUseCase.AssociateVehicleTypeCommand(ruleId, vehicleTypeId));
            when(associateVehicleTypeUseCase.execute(any()))
                    .thenThrow(new MaintenanceRuleNotFoundException("Regla no encontrada con id: " + ruleId));

            mockMvc.perform(post("/api/maintenance-rules/{id}/vehicle-types", ruleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("409 — duplicate association")
        void returns409WhenDuplicate() throws Exception {
            UUID ruleId = UUID.randomUUID();
            UUID vehicleTypeId = UUID.randomUUID();

            AssociateVehicleTypeRequest request = AssociateVehicleTypeRequest.builder()
                    .vehicleTypeId(vehicleTypeId)
                    .build();

            when(associateVehicleTypeWebMapper.toCommand(any(), any())).thenReturn(
                    new AssociateVehicleTypeUseCase.AssociateVehicleTypeCommand(ruleId, vehicleTypeId));
            when(associateVehicleTypeUseCase.execute(any()))
                    .thenThrow(new DuplicateAssociationException("La regla ya está asociada a ese tipo de vehículo"));

            mockMvc.perform(post("/api/maintenance-rules/{id}/vehicle-types", ruleId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }
}