package com.fleetguard.rulesalerts.application.service;

import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceCommand;
import com.fleetguard.rulesalerts.application.ports.in.RegisterMaintenanceUseCase.RegisterMaintenanceResponse;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceAlertRepositoryPort;
import com.fleetguard.rulesalerts.application.ports.out.MaintenanceRecordRepositoryPort;
import com.fleetguard.rulesalerts.domain.exception.AlertNotFoundException;
import com.fleetguard.rulesalerts.domain.exception.InvalidMaintenanceException;
import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.domain.model.maintenance.MaintenanceRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegisterMaintenanceService")
class RegisterMaintenanceServiceTest {

    @Mock private MaintenanceRecordRepositoryPort maintenanceRecordRepositoryPort;
    @Mock private MaintenanceAlertRepositoryPort maintenanceAlertRepositoryPort;

    @InjectMocks
    private RegisterMaintenanceService service;

    private UUID alertId;
    private MaintenanceAlert alert;

    @BeforeEach
    void setUp() {
        alertId = UUID.randomUUID();
        alert = new MaintenanceAlert(
                alertId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "PENDING",
                LocalDateTime.now(),
                50000L
        );
    }

    private RegisterMaintenanceCommand validCommand() {
        return new RegisterMaintenanceCommand(
                "ABC-1234",
                alertId,
                "Oil Change",
                "Routine oil change",
                new BigDecimal("50.00"),
                "AutoShop",
                LocalDateTime.now().minusDays(1),
                45000L,
                "Juan"
        );
    }

    private void stubAlertFound() {
        when(maintenanceAlertRepositoryPort.findById(alertId))
                .thenReturn(Optional.of(alert));
    }

    private void stubSaveRecord() {
        when(maintenanceRecordRepositoryPort.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
        when(maintenanceAlertRepositoryPort.save(any()))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    @DisplayName("Happy path")
    class HappyPath {

        @Test
        @DisplayName("registers maintenance and resolves alert")
        void registersMaintenanceAndResolvesAlert() {
            stubAlertFound();
            stubSaveRecord();

            RegisterMaintenanceResponse response = service.execute(validCommand());

            assertThat(response).isNotNull();
            assertThat(response.serviceType()).isEqualTo("Oil Change");
            assertThat(response.plate()).isEqualTo("ABC-1234");
            assertThat(response.vehicleId()).isEqualTo(alert.getVehicleId());
            assertThat(response.alertId()).isEqualTo(alertId);
            assertThat(response.mileageAtService()).isEqualTo(45000L);
            assertThat(response.recordedBy()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("marks alert as RESOLVED after maintenance")
        void marksAlertAsResolved() {
            stubAlertFound();
            stubSaveRecord();

            service.execute(validCommand());

            ArgumentCaptor<MaintenanceAlert> captor =
                    ArgumentCaptor.forClass(MaintenanceAlert.class);
            verify(maintenanceAlertRepositoryPort, times(1)).save(captor.capture());
            assertThat(captor.getValue().getStatus()).isEqualTo("RESOLVED");
            assertThat(captor.getValue().getId()).isEqualTo(alertId);
        }

        @Test
        @DisplayName("uses performedAt from command when provided")
        void usesProvidedPerformedAt() {
            LocalDateTime performedAt = LocalDateTime.now().minusDays(3);
            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, "Oil Change", "desc",
                    BigDecimal.ZERO, "Shop", performedAt, 45000L, "Juan");

            stubAlertFound();
            stubSaveRecord();

            RegisterMaintenanceResponse response = service.execute(command);

            assertThat(response.performedAt()).isEqualTo(performedAt);
        }

        @Test
        @DisplayName("uses current time as performedAt when not provided")
        void usesCurrentTimeWhenPerformedAtIsNull() {
            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, "Oil Change", "desc",
                    BigDecimal.ZERO, "Shop", null, 45000L, "Juan");

            stubAlertFound();
            stubSaveRecord();

            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            RegisterMaintenanceResponse response = service.execute(command);
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            assertThat(response.performedAt()).isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("Validation — serviceType")
    class ServiceTypeValidation {

        @ParameterizedTest(name = "rejects serviceType [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("rejects null, empty and blank serviceType")
        void rejectsInvalidServiceType(String serviceType) {
            stubAlertFound();

            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, serviceType, "desc",
                    BigDecimal.ZERO, "Shop",
                    LocalDateTime.now().minusDays(1), 45000L, "Juan");

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El tipo de servicio es obligatorio");

            verify(maintenanceRecordRepositoryPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Validation — performedAt")
    class PerformedAtValidation {

        @Test
        @DisplayName("rejects future performedAt — boundary: tomorrow")
        void rejectsFutureDate() {
            stubAlertFound();

            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, "Oil Change", "desc",
                    BigDecimal.ZERO, "Shop",
                    LocalDateTime.now().plusDays(1), 45000L, "Juan");

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("La fecha del servicio no puede ser futura");

            verify(maintenanceRecordRepositoryPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Validation — mileageAtService")
    class MileageValidation {

        @Test
        @DisplayName("rejects mileage of zero — boundary lower invalid")
        void rejectsZeroMileage() {
            stubAlertFound();

            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, "Oil Change", "desc",
                    BigDecimal.ZERO, "Shop",
                    LocalDateTime.now().minusDays(1), 0L, "Juan");

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El kilometraje del servicio debe ser mayor a cero");

            verify(maintenanceRecordRepositoryPort, never()).save(any());
        }

        @Test
        @DisplayName("rejects negative mileage — boundary")
        void rejectsNegativeMileage() {
            stubAlertFound();

            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, "Oil Change", "desc",
                    BigDecimal.ZERO, "Shop",
                    LocalDateTime.now().minusDays(1), -1L, "Juan");

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El kilometraje del servicio debe ser mayor a cero");
        }

        @Test
        @DisplayName("accepts mileage of 1 — boundary lower valid")
        void acceptsMileageOf1() {
            stubAlertFound();
            stubSaveRecord();

            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, "Oil Change", "desc",
                    BigDecimal.ZERO, "Shop",
                    LocalDateTime.now().minusDays(1), 1L, "Juan");

            assertThatNoException().isThrownBy(() -> service.execute(command));
        }
    }

    @Nested
    @DisplayName("Validation — recordedBy")
    class RecordedByValidation {

        @ParameterizedTest(name = "rejects recordedBy [{0}]")
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("rejects null, empty and blank recordedBy")
        void rejectsInvalidRecordedBy(String recordedBy) {
            stubAlertFound();

            RegisterMaintenanceCommand command = new RegisterMaintenanceCommand(
                    "ABC-1234", alertId, "Oil Change", "desc",
                    BigDecimal.ZERO, "Shop",
                    LocalDateTime.now().minusDays(1), 45000L, recordedBy);

            assertThatThrownBy(() -> service.execute(command))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El nombre de quien registra es obligatorio");

            verify(maintenanceRecordRepositoryPort, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Error handling — alert")
    class AlertErrorHandling {

        @Test
        @DisplayName("throws AlertNotFoundException when alertId does not exist")
        void throwsWhenAlertNotFound() {
            when(maintenanceAlertRepositoryPort.findById(alertId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.execute(validCommand()))
                    .isInstanceOf(AlertNotFoundException.class)
                    .hasMessageContaining(alertId.toString());

            verify(maintenanceRecordRepositoryPort, never()).save(any());
        }
    }
}