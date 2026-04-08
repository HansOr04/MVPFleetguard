package com.fleetguard.rulesalerts.infrastructure.web.mapper;

import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeCommand;
import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeResponse;
import com.fleetguard.rulesalerts.infrastructure.web.dto.request.AssociateVehicleTypeRequest;
import com.fleetguard.rulesalerts.infrastructure.web.dto.response.RuleVehicleTypeAssocResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AssociateVehicleTypeWebMapper")
class AssociateVehicleTypeWebMapperTest {

    private final AssociateVehicleTypeWebMapper mapper = new AssociateVehicleTypeWebMapper();

    @Nested
    @DisplayName("toCommand")
    class ToCommand {

        @Test
        @DisplayName("maps ruleId and vehicleTypeId to command")
        void mapsAllFields() {
            UUID ruleId = UUID.randomUUID();
            UUID vehicleTypeId = UUID.randomUUID();

            AssociateVehicleTypeRequest request = AssociateVehicleTypeRequest.builder()
                    .vehicleTypeId(vehicleTypeId)
                    .build();

            AssociateVehicleTypeCommand command = mapper.toCommand(ruleId, request);

            assertThat(command.ruleId()).isEqualTo(ruleId);
            assertThat(command.vehicleTypeId()).isEqualTo(vehicleTypeId);
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("maps all response fields to DTO")
        void mapsAllFields() {
            UUID id = UUID.randomUUID();
            UUID ruleId = UUID.randomUUID();
            UUID vehicleTypeId = UUID.randomUUID();
            LocalDateTime createdAt = LocalDateTime.now();

            AssociateVehicleTypeResponse response = new AssociateVehicleTypeResponse(
                    id, ruleId, vehicleTypeId, createdAt);

            RuleVehicleTypeAssocResponse dto = mapper.toResponse(response);

            assertThat(dto.getId()).isEqualTo(id);
            assertThat(dto.getRuleId()).isEqualTo(ruleId);
            assertThat(dto.getVehicleTypeId()).isEqualTo(vehicleTypeId);
            assertThat(dto.getCreatedAt()).isEqualTo(createdAt);
        }
    }
}