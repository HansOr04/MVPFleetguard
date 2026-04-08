package com.fleetguard.rulesalerts.infrastructure.web.mapper;

import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleCommand;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.CreateMaintenanceRuleRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRuleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MaintenanceRuleWebMapper")
class MaintenanceRuleWebMapperTest {

    private final MaintenanceRuleWebMapper mapper = new MaintenanceRuleWebMapper();

    @Nested
    @DisplayName("toCommand")
    class ToCommand {

        @Test
        @DisplayName("maps all request fields to command")
        void mapsAllFields() {
            CreateMaintenanceRuleRequest request = CreateMaintenanceRuleRequest.builder()
                    .name("Oil Change")
                    .maintenanceType("OIL")
                    .intervalKm(5000)
                    .warningThresholdKm(500)
                    .build();

            CreateMaintenanceRuleCommand command = mapper.toCommand(request);

            assertThat(command.name()).isEqualTo("Oil Change");
            assertThat(command.maintenanceType()).isEqualTo("OIL");
            assertThat(command.intervalKm()).isEqualTo(5000);
            assertThat(command.warningThresholdKm()).isEqualTo(500);
        }

        @Test
        @DisplayName("maps null warningThresholdKm — optional field")
        void mapsNullWarningThreshold() {
            CreateMaintenanceRuleRequest request = CreateMaintenanceRuleRequest.builder()
                    .name("Oil Change")
                    .maintenanceType("OIL")
                    .intervalKm(5000)
                    .build();

            CreateMaintenanceRuleCommand command = mapper.toCommand(request);

            assertThat(command.warningThresholdKm()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("maps all response fields to DTO")
        void mapsAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();

            CreateMaintenanceRuleResponse response = new CreateMaintenanceRuleResponse(
                    id, "Oil Change", "OIL",
                    5000, 500, "ACTIVE", now, now);

            MaintenanceRuleResponse dto = mapper.toResponse(response);

            assertThat(dto.getId()).isEqualTo(id);
            assertThat(dto.getName()).isEqualTo("Oil Change");
            assertThat(dto.getMaintenanceType()).isEqualTo("OIL");
            assertThat(dto.getIntervalKm()).isEqualTo(5000);
            assertThat(dto.getWarningThresholdKm()).isEqualTo(500);
            assertThat(dto.getStatus()).isEqualTo("ACTIVE");
            assertThat(dto.getCreatedAt()).isEqualTo(now);
            assertThat(dto.getUpdatedAt()).isEqualTo(now);
        }
    }
}