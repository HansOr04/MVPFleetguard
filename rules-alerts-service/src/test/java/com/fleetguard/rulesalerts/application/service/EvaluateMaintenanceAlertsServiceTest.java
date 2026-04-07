package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.application.ports.out.RuleVehicleTypeAssocQueryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluateMaintenanceAlertsService")
class EvaluateMaintenanceAlertsServiceTest {

    @Mock private RuleVehicleTypeAssocQueryPort assocQueryPort;
    @Mock private MaintenanceRuleQueryPort ruleQueryPort;
    @Mock private MaintenanceAlertRepositoryPort alertRepositoryPort;

    @InjectMocks
    private EvaluateMaintenanceAlertsService service;

    private UUID vehicleId;
    private UUID vehicleTypeId;
    private UUID ruleId;
    private MaintenanceRule rule;
    private RuleVehicleTypeAssoc association;

    @BeforeEach
    void setUp() {
        vehicleId = UUID.randomUUID();
        vehicleTypeId = UUID.randomUUID();
        ruleId = UUID.randomUUID();

        rule = new MaintenanceRule(
                ruleId, "Oil Change", "OIL",
                5000, 500, "ACTIVE",
                LocalDateTime.now(), LocalDateTime.now()
        );

        association = new RuleVehicleTypeAssoc(
                UUID.randomUUID(), ruleId, vehicleTypeId, LocalDateTime.now()
        );
    }

    private void stubAssocAndRule() {
        when(assocQueryPort.findByVehicleTypeId(vehicleTypeId)).thenReturn(List.of(association));
        when(ruleQueryPort.findById(ruleId)).thenReturn(Optional.of(rule));
    }

    @Nested
    @DisplayName("Skip evaluation")
    class SkipEvaluation {

        @Test
        @DisplayName("skips when vehicle is INACTIVE")
        void skipsInactiveVehicle() {
            service.evaluate(vehicleId, vehicleTypeId, 4800L, "INACTIVE");

            verifyNoInteractions(assocQueryPort, ruleQueryPort, alertRepositoryPort);
        }

        @Test
        @DisplayName("skips when no associations found for vehicleTypeId")
        void skipsWhenNoAssociations() {
            when(assocQueryPort.findByVehicleTypeId(vehicleTypeId)).thenReturn(List.of());

            service.evaluate(vehicleId, vehicleTypeId, 4800L, "ACTIVE");

            verifyNoInteractions(ruleQueryPort, alertRepositoryPort);
        }

        @Test
        @DisplayName("skips rule when MaintenanceRule not found — logs warn and continues")
        void skipsWhenRuleNotFound() {
            when(assocQueryPort.findByVehicleTypeId(vehicleTypeId)).thenReturn(List.of(association));
            when(ruleQueryPort.findById(ruleId)).thenReturn(Optional.empty());

            service.evaluate(vehicleId, vehicleTypeId, 4800L, "ACTIVE");

            verify(alertRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("skips alert creation and repo when kmRemaining > threshold and previousDueAtKm=0")
        void skipsWhenFarFromMaintenanceAndFirstInterval() {
            stubAssocAndRule();

            service.evaluate(vehicleId, vehicleTypeId, 100L, "ACTIVE");

            verifyNoInteractions(alertRepositoryPort);
        }
    }

    @Nested
    @DisplayName("Alert creation")
    class AlertCreation {

        @Test
        @DisplayName("creates PENDING alert — boundary kmRemaining=167 (> threshold/3=166)")
        void createsPendingAtBoundary167() {
            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.empty());

            service.evaluate(vehicleId, vehicleTypeId, 4833L, "ACTIVE");

            ArgumentCaptor<MaintenanceAlert> captor = ArgumentCaptor.forClass(MaintenanceAlert.class);
            verify(alertRepositoryPort, times(1)).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo("PENDING");
            assertThat(captor.getValue().getDueAtKm()).isEqualTo(5000L);
            assertThat(captor.getValue().getVehicleId()).isEqualTo(vehicleId);
        }

        @Test
        @DisplayName("creates WARNING alert — boundary kmRemaining=166 (<= threshold/3=166)")
        void createsWarningAtBoundary166() {
            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.empty());

            service.evaluate(vehicleId, vehicleTypeId, 4834L, "ACTIVE");

            ArgumentCaptor<MaintenanceAlert> captor = ArgumentCaptor.forClass(MaintenanceAlert.class);
            verify(alertRepositoryPort, times(1)).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo("WARNING");
        }
    }

    @Nested
    @DisplayName("Alert update")
    class AlertUpdate {

        @Test
        @DisplayName("updates alert from PENDING to WARNING when status changes")
        void updatesWhenStatusDiffers() {
            MaintenanceAlert existingPending = new MaintenanceAlert(
                    UUID.randomUUID(), vehicleId, vehicleTypeId, ruleId,
                    "PENDING", LocalDateTime.now(), 5000L);

            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.of(existingPending));

            service.evaluate(vehicleId, vehicleTypeId, 4834L, "ACTIVE");

            ArgumentCaptor<MaintenanceAlert> captor = ArgumentCaptor.forClass(MaintenanceAlert.class);
            verify(alertRepositoryPort, times(1)).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo("WARNING");
            assertThat(captor.getValue().getId()).isEqualTo(existingPending.getId());
        }

        @Test
        @DisplayName("does NOT update when existing alert has same status")
        void doesNotUpdateWhenStatusIsSame() {
            MaintenanceAlert existingPending = new MaintenanceAlert(
                    UUID.randomUUID(), vehicleId, vehicleTypeId, ruleId,
                    "PENDING", LocalDateTime.now(), 5000L);

            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.of(existingPending));

            service.evaluate(vehicleId, vehicleTypeId, 4833L, "ACTIVE");

            verify(alertRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("does NOT update when existing alert is already RESOLVED")
        void doesNotUpdateWhenResolved() {
            MaintenanceAlert resolved = new MaintenanceAlert(
                    UUID.randomUUID(), vehicleId, vehicleTypeId, ruleId,
                    "RESOLVED", LocalDateTime.now(), 5000L);

            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.of(resolved));

            service.evaluate(vehicleId, vehicleTypeId, 4833L, "ACTIVE");

            verify(alertRepositoryPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Previous cycle — evaluatePreviousCycle")
    class PreviousCycle {

        @Test
        @DisplayName("marks previous cycle as OVERDUE when currentMileage >= previousDueAtKm")
        void marksPreviousCycleAsOverdue() {
            MaintenanceAlert previousAlert = new MaintenanceAlert(
                    UUID.randomUUID(), vehicleId, vehicleTypeId, ruleId,
                    "PENDING", LocalDateTime.now(), 5000L);

            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.of(previousAlert));

            service.evaluate(vehicleId, vehicleTypeId, 5001L, "ACTIVE");

            ArgumentCaptor<MaintenanceAlert> captor = ArgumentCaptor.forClass(MaintenanceAlert.class);
            verify(alertRepositoryPort, times(1)).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo("OVERDUE");
        }

        @Test
        @DisplayName("does NOT mark as OVERDUE when previous cycle is already RESOLVED")
        void doesNotMarkOverdueWhenAlreadyResolved() {
            MaintenanceAlert resolvedPrevious = new MaintenanceAlert(
                    UUID.randomUUID(), vehicleId, vehicleTypeId, ruleId,
                    "RESOLVED", LocalDateTime.now(), 5000L);

            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.of(resolvedPrevious));

            service.evaluate(vehicleId, vehicleTypeId, 5001L, "ACTIVE");

            verify(alertRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("does NOT mark as OVERDUE when previous cycle is already OVERDUE")
        void doesNotMarkOverdueWhenAlreadyOverdue() {
            MaintenanceAlert alreadyOverdue = new MaintenanceAlert(
                    UUID.randomUUID(), vehicleId, vehicleTypeId, ruleId,
                    "OVERDUE", LocalDateTime.now(), 5000L);

            stubAssocAndRule();
            when(alertRepositoryPort.findByVehicleIdAndRuleIdAndDueAtKm(vehicleId, ruleId, 5000L))
                    .thenReturn(Optional.of(alreadyOverdue));

            service.evaluate(vehicleId, vehicleTypeId, 5001L, "ACTIVE");

            verify(alertRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("does NOT interact with repo when previousDueAtKm=0 — first interval boundary")
        void doesNotInteractWithRepoOnFirstInterval() {
            stubAssocAndRule();

            service.evaluate(vehicleId, vehicleTypeId, 100L, "ACTIVE");

            verifyNoInteractions(alertRepositoryPort);
        }
    }
}