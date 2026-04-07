package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.GetAlertsByVehicleUseCase.AlertDetail;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
import com.fleetguard.rulesalerts.application.ports.out.VehicleQueryPort;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAlertsByVehicleService")
class GetAlertsByVehicleServiceTest {

    @Mock private VehicleQueryPort vehicleQueryPort;
    @Mock private MaintenanceAlertRepositoryPort alertRepositoryPort;
    @Mock private MaintenanceRuleQueryPort ruleQueryPort;

    @InjectMocks
    private GetAlertsByVehicleService service;

    private UUID vehicleId;
    private UUID ruleId;
    private MaintenanceAlert alert;
    private MaintenanceRule rule;

    @BeforeEach
    void setUp() {
        vehicleId = UUID.randomUUID();
        ruleId = UUID.randomUUID();
        alert = new MaintenanceAlert(
                UUID.randomUUID(), vehicleId, UUID.randomUUID(), ruleId,
                "PENDING", LocalDateTime.now(), 5000L);
        rule = new MaintenanceRule(
                ruleId, "Oil Change", "OIL",
                5000, 500, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("returns active alerts for vehicle")
        void returnsActiveAlerts() {
            when(vehicleQueryPort.findVehicleIdByPlate("ABC-1234"))
                    .thenReturn(Optional.of(vehicleId));
            when(alertRepositoryPort.findActiveByVehicleId(vehicleId))
                    .thenReturn(List.of(alert));
            when(ruleQueryPort.findById(ruleId)).thenReturn(Optional.of(rule));

            List<AlertDetail> result = service.execute("ABC-1234");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("PENDING");
            assertThat(result.get(0).ruleName()).isEqualTo("Oil Change");
            assertThat(result.get(0).vehicleId()).isEqualTo(vehicleId);
        }

        @Test
        @DisplayName("returns empty list when vehicle not found — no further queries")
        void returnsEmptyWhenVehicleNotFound() {
            when(vehicleQueryPort.findVehicleIdByPlate("ZZZ-000"))
                    .thenReturn(Optional.empty());

            List<AlertDetail> result = service.execute("ZZZ-000");

            assertThat(result).isEmpty();
            verify(alertRepositoryPort, never()).findActiveByVehicleId(any());
        }

        @Test
        @DisplayName("returns empty list when vehicle has no active alerts")
        void returnsEmptyWhenNoAlerts() {
            when(vehicleQueryPort.findVehicleIdByPlate("ABC-1234"))
                    .thenReturn(Optional.of(vehicleId));
            when(alertRepositoryPort.findActiveByVehicleId(vehicleId))
                    .thenReturn(List.of());

            List<AlertDetail> result = service.execute("ABC-1234");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("uses fallback rule name when rule not found")
        void usesFallbackRuleName() {
            when(vehicleQueryPort.findVehicleIdByPlate("ABC-1234"))
                    .thenReturn(Optional.of(vehicleId));
            when(alertRepositoryPort.findActiveByVehicleId(vehicleId))
                    .thenReturn(List.of(alert));
            when(ruleQueryPort.findById(ruleId)).thenReturn(Optional.empty());

            List<AlertDetail> result = service.execute("ABC-1234");

            assertThat(result.get(0).ruleName()).isEqualTo("Regla desconocida");
        }

        @Test
        @DisplayName("normalizes plate to uppercase before querying")
        void normalizesPlate() {
            when(vehicleQueryPort.findVehicleIdByPlate("ABC-1234"))
                    .thenReturn(Optional.of(vehicleId));
            when(alertRepositoryPort.findActiveByVehicleId(vehicleId))
                    .thenReturn(List.of());

            service.execute("abc-1234");

            verify(vehicleQueryPort).findVehicleIdByPlate("ABC-1234");
        }
    }
}