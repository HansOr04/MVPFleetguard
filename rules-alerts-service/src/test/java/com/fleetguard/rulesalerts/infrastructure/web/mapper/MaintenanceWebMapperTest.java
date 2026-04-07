package com.fleetguard.rulesalerts.infrastructure.web.mapper;

import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceCommand;
import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.RegisterMaintenanceRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.MaintenanceRecordResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MaintenanceWebMapper")
class MaintenanceWebMapperTest {

    private final MaintenanceWebMapper mapper = new MaintenanceWebMapper();

    private final UUID alertId = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();
    private final UUID ruleId = UUID.randomUUID();
    private final LocalDateTime performedAt = LocalDateTime.now().minusDays(1);

    @Nested
    @DisplayName("toCommand")
    class ToCommand {

        @Test
        @DisplayName("maps plate and all request fields to command")
        void mapsAllFields() {
            RegisterMaintenanceRequest request = RegisterMaintenanceRequest.builder()
                    .alertId(alertId)
                    .serviceType("Oil Change")
                    .description("Routine")
                    .cost(new BigDecimal("50.00"))
                    .provider("AutoShop")
                    .performedAt(performedAt)
                    .mileageAtService(45000L)
                    .recordedBy("Juan")
                    .build();

            RegisterMaintenanceCommand command = mapper.toCommand("ABC-1234", request);

            assertThat(command.plate()).isEqualTo("ABC-1234");
            assertThat(command.alertId()).isEqualTo(alertId);
            assertThat(command.serviceType()).isEqualTo("Oil Change");
            assertThat(command.description()).isEqualTo("Routine");
            assertThat(command.cost()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(command.provider()).isEqualTo("AutoShop");
            assertThat(command.performedAt()).isEqualTo(performedAt);
            assertThat(command.mileageAtService()).isEqualTo(45000L);
            assertThat(command.recordedBy()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("maps null optional fields — description, cost, provider")
        void mapsNullOptionalFields() {
            RegisterMaintenanceRequest request = RegisterMaintenanceRequest.builder()
                    .alertId(alertId)
                    .serviceType("Oil Change")
                    .performedAt(performedAt)
                    .mileageAtService(45000L)
                    .recordedBy("Juan")
                    .build();

            RegisterMaintenanceCommand command = mapper.toCommand("ABC-1234", request);

            assertThat(command.description()).isNull();
            assertThat(command.cost()).isNull();
            assertThat(command.provider()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("maps all response fields to DTO")
        void mapsAllFields() {
            UUID id = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now();

            RegisterMaintenanceResponse response = new RegisterMaintenanceResponse(
                    id, vehicleId, "ABC-1234", alertId, ruleId,
                    "Oil Change", "Routine",
                    new BigDecimal("50.00"), "AutoShop",
                    performedAt, 45000L, "Juan", createdAt);

            MaintenanceRecordResponse dto = mapper.toResponse(response);

            assertThat(dto.getId()).isEqualTo(id);
            assertThat(dto.getVehicleId()).isEqualTo(vehicleId);
            assertThat(dto.getPlate()).isEqualTo("ABC-1234");
            assertThat(dto.getAlertId()).isEqualTo(alertId);
            assertThat(dto.getRuleId()).isEqualTo(ruleId);
            assertThat(dto.getServiceType()).isEqualTo("Oil Change");
            assertThat(dto.getDescription()).isEqualTo("Routine");
            assertThat(dto.getCost()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(dto.getProvider()).isEqualTo("AutoShop");
            assertThat(dto.getPerformedAt()).isEqualTo(performedAt);
            assertThat(dto.getMileageAtService()).isEqualTo(45000L);
            assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        }
    }
}