package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleCommand;
import com.fleetguard.rulesalerts.application.ports.in.CreateMaintenanceRuleUseCase.CreateMaintenanceRuleResponse;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRuleRepositoryPort;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRuleConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateMaintenanceRuleService")
class CreateMaintenanceRuleServiceTest {

    @Mock private MaintenanceRuleRepositoryPort repositoryPort;

    @InjectMocks
    private CreateMaintenanceRuleService service;

    @BeforeEach
    void setUp() {
        when(repositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private CreateMaintenanceRuleCommand commandWith(Integer warningThresholdKm) {
        return new CreateMaintenanceRuleCommand(
                "Oil Change", "OIL", 5000, warningThresholdKm);
    }

    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("creates rule with all fields mapped correctly")
        void createsMapsFieldsCorrectly() {
            CreateMaintenanceRuleResponse response = service.execute(commandWith(500));

            assertThat(response.name()).isEqualTo("Oil Change");
            assertThat(response.maintenanceType()).isEqualTo("OIL");
            assertThat(response.intervalKm()).isEqualTo(5000);
            assertThat(response.warningThresholdKm()).isEqualTo(500);
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.id()).isNotNull();
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("persists rule via repository")
        void persistsViaRepository() {
            service.execute(commandWith(500));

            ArgumentCaptor<MaintenanceRule> captor = ArgumentCaptor.forClass(MaintenanceRule.class);
            verify(repositoryPort, times(1)).save(captor.capture());
            assertThat(captor.getValue().getName()).isEqualTo("Oil Change");
            assertThat(captor.getValue().getStatus()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("warningThresholdKm — default resolution")
    class WarningThresholdResolution {

        @Test
        @DisplayName("uses default when warningThresholdKm is null")
        void usesDefaultWhenNull() {
            CreateMaintenanceRuleResponse response = service.execute(commandWith(null));

            assertThat(response.warningThresholdKm())
                    .isEqualTo(MaintenanceRuleConstants.DEFAULT_WARNING_THRESHOLD_KM);
        }

        @ParameterizedTest(name = "uses default when warningThresholdKm={0} — boundary invalid")
        @ValueSource(ints = {0, -1, -500})
        @DisplayName("uses default when warningThresholdKm <= 0")
        void usesDefaultWhenZeroOrNegative(int threshold) {
            CreateMaintenanceRuleResponse response = service.execute(commandWith(threshold));

            assertThat(response.warningThresholdKm())
                    .isEqualTo(MaintenanceRuleConstants.DEFAULT_WARNING_THRESHOLD_KM);
        }

        @Test
        @DisplayName("uses provided value when warningThresholdKm=1 — boundary lower valid")
        void usesProvidedValueAt1() {
            CreateMaintenanceRuleResponse response = service.execute(commandWith(1));

            assertThat(response.warningThresholdKm()).isEqualTo(1);
        }

        @Test
        @DisplayName("uses provided value when warningThresholdKm=500")
        void usesProvidedValueAt500() {
            CreateMaintenanceRuleResponse response = service.execute(commandWith(500));

            assertThat(response.warningThresholdKm()).isEqualTo(500);
        }
    }
}