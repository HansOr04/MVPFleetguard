package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.GetAlertsUseCase.AlertDetail;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleQueryPort;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAlertsService")
class GetAlertsServiceTest {

    @Mock private MaintenanceAlertRepositoryPort alertRepositoryPort;
    @Mock private MaintenanceRuleQueryPort ruleQueryPort;

    @InjectMocks
    private GetAlertsService service;

    private UUID ruleId;
    private MaintenanceAlert alert;
    private MaintenanceRule rule;

    @BeforeEach
    void setUp() {
        ruleId = UUID.randomUUID();
        alert = new MaintenanceAlert(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), ruleId,
                "PENDING", LocalDateTime.now(), 5000L);
        rule = new MaintenanceRule(
                ruleId, "Oil Change", "OIL",
                5000, 500, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("returns all active alerts when status is null")
        void returnsAllActiveWhenStatusNull() {
            when(alertRepositoryPort.findAllActive()).thenReturn(List.of(alert));
            when(ruleQueryPort.findById(ruleId)).thenReturn(Optional.of(rule));

            List<AlertDetail> result = service.execute(null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("PENDING");
            assertThat(result.get(0).ruleName()).isEqualTo("Oil Change");
        }

        @Test
        @DisplayName("returns all active alerts when status is blank")
        void returnsAllActiveWhenStatusBlank() {
            when(alertRepositoryPort.findAllActive()).thenReturn(List.of(alert));
            when(ruleQueryPort.findById(ruleId)).thenReturn(Optional.of(rule));

            List<AlertDetail> result = service.execute("   ");

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("filters by status when status is provided")
        void filtersByStatus() {
            when(alertRepositoryPort.findByStatus("WARNING")).thenReturn(List.of());

            List<AlertDetail> result = service.execute("WARNING");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("uses fallback rule name when rule not found")
        void usesFallbackRuleName() {
            when(alertRepositoryPort.findAllActive()).thenReturn(List.of(alert));
            when(ruleQueryPort.findById(ruleId)).thenReturn(Optional.empty());

            List<AlertDetail> result = service.execute(null);

            assertThat(result.get(0).ruleName()).isEqualTo("Regla desconocida");
        }

        @Test
        @DisplayName("returns empty list when no alerts")
        void returnsEmptyList() {
            when(alertRepositoryPort.findAllActive()).thenReturn(List.of());

            List<AlertDetail> result = service.execute(null);

            assertThat(result).isEmpty();
        }
    }
}