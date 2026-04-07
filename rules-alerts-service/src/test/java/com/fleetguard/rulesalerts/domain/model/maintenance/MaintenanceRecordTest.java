package com.fleetguard.rulesalerts.domain.model.maintenance;

import com.fleetguard.rulesalerts.domain.exception.InvalidMaintenanceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MaintenanceRecord")
class MaintenanceRecordTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID VEHICLE_ID = UUID.randomUUID();
    private static final UUID ALERT_ID = UUID.randomUUID();
    private static final UUID RULE_ID = UUID.randomUUID();
    private static final LocalDateTime PAST = LocalDateTime.now().minusDays(1);

    private MaintenanceRecord valid() {
        return new MaintenanceRecord(
                ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                "Oil Change", "desc",
                new BigDecimal("50.00"), "Shop",
                PAST, 45000L, "Juan");
    }

    @Nested
    @DisplayName("Construction — happy path")
    class Construction {

        @Test
        @DisplayName("creates record with all fields correctly assigned")
        void createsWithAllFields() {
            MaintenanceRecord record = valid();

            assertThat(record.getId()).isEqualTo(ID);
            assertThat(record.getVehicleId()).isEqualTo(VEHICLE_ID);
            assertThat(record.getAlertId()).isEqualTo(ALERT_ID);
            assertThat(record.getRuleId()).isEqualTo(RULE_ID);
            assertThat(record.getServiceType()).isEqualTo("Oil Change");
            assertThat(record.getDescription()).isEqualTo("desc");
            assertThat(record.getCost()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(record.getProvider()).isEqualTo("Shop");
            assertThat(record.getPerformedAt()).isEqualTo(PAST);
            assertThat(record.getMileageAtService()).isEqualTo(45000L);
            assertThat(record.getRecordedBy()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("accepts null optional fields — description, cost, provider")
        void acceptsNullOptionalFields() {
            assertThatNoException().isThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null,
                            null, null,
                            PAST, 45000L, "Juan"));
        }

        @Test
        @DisplayName("accepts null performedAt — will be resolved by service")
        void acceptsNullPerformedAt() {
            assertThatNoException().isThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null,
                            null, null,
                            null, 45000L, "Juan"));
        }

        @Test
        @DisplayName("accepts mileage of 1 — boundary lower valid")
        void acceptsMileageOf1() {
            assertThatNoException().isThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null, null, null,
                            PAST, 1L, "Juan"));
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
            assertThatThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            serviceType, null, null, null,
                            PAST, 45000L, "Juan"))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El tipo de servicio es obligatorio");
        }
    }

    @Nested
    @DisplayName("Validation — performedAt")
    class PerformedAtValidation {

        @Test
        @DisplayName("rejects future performedAt — boundary tomorrow")
        void rejectsFutureDate() {
            assertThatThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null, null, null,
                            LocalDateTime.now().plusDays(1), 45000L, "Juan"))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("La fecha del servicio no puede ser futura");
        }

        @Test
        @DisplayName("accepts past performedAt — boundary valid")
        void acceptsPastDate() {
            assertThatNoException().isThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null, null, null,
                            LocalDateTime.now().minusSeconds(1), 45000L, "Juan"));
        }
    }

    @Nested
    @DisplayName("Validation — mileageAtService")
    class MileageValidation {

        @Test
        @DisplayName("rejects mileage of zero — boundary lower invalid")
        void rejectsZero() {
            assertThatThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null, null, null,
                            PAST, 0L, "Juan"))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El kilometraje del servicio debe ser mayor a cero");
        }

        @Test
        @DisplayName("rejects negative mileage")
        void rejectsNegative() {
            assertThatThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null, null, null,
                            PAST, -1L, "Juan"))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El kilometraje del servicio debe ser mayor a cero");
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
            assertThatThrownBy(() ->
                    new MaintenanceRecord(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "Oil Change", null, null, null,
                            PAST, 45000L, recordedBy))
                    .isInstanceOf(InvalidMaintenanceException.class)
                    .hasMessage("El nombre de quien registra es obligatorio");
        }
    }

    @Nested
    @DisplayName("reconstitute — sin validaciones")
    class Reconstitute {

        @Test
        @DisplayName("reconstitutes with invalid data without throwing — datos ya persistidos")
        void reconstituteSkipsValidation() {
            assertThatNoException().isThrownBy(() ->
                    MaintenanceRecord.reconstitute(
                            ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                            "", null, null, null,
                            PAST, 0L, ""));
        }

        @Test
        @DisplayName("reconstitutes all fields correctly")
        void reconstitutesMapsAllFields() {
            MaintenanceRecord record = MaintenanceRecord.reconstitute(
                    ID, VEHICLE_ID, ALERT_ID, RULE_ID,
                    "Oil Change", "desc",
                    new BigDecimal("30.00"), "Shop",
                    PAST, 60000L, "Pedro");

            assertThat(record.getServiceType()).isEqualTo("Oil Change");
            assertThat(record.getMileageAtService()).isEqualTo(60000L);
            assertThat(record.getRecordedBy()).isEqualTo("Pedro");
        }
    }
}