package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeCommand;
import com.fleetguard.rulesalerts.application.ports.in.AssociateVehicleTypeUseCase.AssociateVehicleTypeResponse;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.RuleVehicleTypeAssocRepositoryPort;
import com.fleetguard.rulesalerts.domain.exception.DuplicateAssociationException;
import com.fleetguard.rulesalerts.domain.exception.MaintenanceRuleNotFoundException;
import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AssociateVehicleTypeService")
class AssociateVehicleTypeServiceTest {

    @Mock private MaintenanceRuleRepositoryPort maintenanceRuleRepositoryPort;
    @Mock private RuleVehicleTypeAssocRepositoryPort assocRepositoryPort;

    @InjectMocks
    private AssociateVehicleTypeService service;

    private UUID ruleId;
    private UUID vehicleTypeId;

    private AssociateVehicleTypeCommand command() {
        ruleId = UUID.randomUUID();
        vehicleTypeId = UUID.randomUUID();
        return new AssociateVehicleTypeCommand(ruleId, vehicleTypeId);
    }

    // ─────────────────────────────────────────────
    // Happy path
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("creates association and returns mapped response")
        void createsAssociation() {
            AssociateVehicleTypeCommand cmd = command();
            when(maintenanceRuleRepositoryPort.existsById(ruleId)).thenReturn(true);
            when(assocRepositoryPort.existsByRuleIdAndVehicleTypeId(ruleId, vehicleTypeId))
                    .thenReturn(false);
            when(assocRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            AssociateVehicleTypeResponse response = service.execute(cmd);

            assertThat(response.ruleId()).isEqualTo(ruleId);
            assertThat(response.vehicleTypeId()).isEqualTo(vehicleTypeId);
            assertThat(response.id()).isNotNull();
            assertThat(response.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("persists association via repository")
        void persistsViaRepository() {
            AssociateVehicleTypeCommand cmd = command();
            when(maintenanceRuleRepositoryPort.existsById(ruleId)).thenReturn(true);
            when(assocRepositoryPort.existsByRuleIdAndVehicleTypeId(ruleId, vehicleTypeId))
                    .thenReturn(false);
            when(assocRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

            service.execute(cmd);

            ArgumentCaptor<RuleVehicleTypeAssoc> captor =
                    ArgumentCaptor.forClass(RuleVehicleTypeAssoc.class);
            verify(assocRepositoryPort, times(1)).save(captor.capture());
            assertThat(captor.getValue().getRuleId()).isEqualTo(ruleId);
            assertThat(captor.getValue().getVehicleTypeId()).isEqualTo(vehicleTypeId);
            assertThat(captor.getValue().getCreatedAt()).isNotNull();
        }
    }

    // ─────────────────────────────────────────────
    // Error handling
    // ─────────────────────────────────────────────

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("throws MaintenanceRuleNotFoundException when rule does not exist")
        void throwsWhenRuleNotFound() {
            AssociateVehicleTypeCommand cmd = command();
            when(maintenanceRuleRepositoryPort.existsById(ruleId)).thenReturn(false);

            assertThatThrownBy(() -> service.execute(cmd))
                    .isInstanceOf(MaintenanceRuleNotFoundException.class)
                    .hasMessageContaining(ruleId.toString());

            verify(assocRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("throws DuplicateAssociationException when association already exists")
        void throwsWhenDuplicate() {
            AssociateVehicleTypeCommand cmd = command();
            when(maintenanceRuleRepositoryPort.existsById(ruleId)).thenReturn(true);
            when(assocRepositoryPort.existsByRuleIdAndVehicleTypeId(ruleId, vehicleTypeId))
                    .thenReturn(true);

            assertThatThrownBy(() -> service.execute(cmd))
                    .isInstanceOf(DuplicateAssociationException.class)
                    .hasMessage("La regla ya está asociada a ese tipo de vehículo");

            verify(assocRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("does not check for duplicate when rule does not exist — short circuit")
        void shortCircuitsWhenRuleNotFound() {
            AssociateVehicleTypeCommand cmd = command();
            when(maintenanceRuleRepositoryPort.existsById(ruleId)).thenReturn(false);

            assertThatThrownBy(() -> service.execute(cmd))
                    .isInstanceOf(MaintenanceRuleNotFoundException.class);

            verify(assocRepositoryPort, never()).existsByRuleIdAndVehicleTypeId(any(), any());
        }
    }
}