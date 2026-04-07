package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.maintenance.MaintenanceRecord;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRecordJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MaintenanceRecordPersistenceMapper")
class MaintenanceRecordPersistenceMapperTest {

    private final UUID id = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();
    private final UUID alertId = UUID.randomUUID();
    private final UUID ruleId = UUID.randomUUID();
    private final LocalDateTime performedAt = LocalDateTime.now();

    @Nested
    @DisplayName("toJpaEntity")
    class ToJpaEntity {

        @Test
        @DisplayName("maps all domain fields to JPA entity correctly")
        void mapsAllFields() {
            MaintenanceRecord domain = new MaintenanceRecord(
                    id, vehicleId, alertId, ruleId,
                    "Oil Change", "Routine oil change",
                    new BigDecimal("75.50"), "AutoShop",
                    performedAt, 45000L, "Juan");

            MaintenanceRecordJpaEntity entity =
                    MaintenanceRecordPersistenceMapper.toJpaEntity(domain);

            assertThat(entity.getId()).isEqualTo(id);
            assertThat(entity.getVehicleId()).isEqualTo(vehicleId);
            assertThat(entity.getAlertId()).isEqualTo(alertId);
            assertThat(entity.getRuleId()).isEqualTo(ruleId);
            assertThat(entity.getServiceType()).isEqualTo("Oil Change");
            assertThat(entity.getDescription()).isEqualTo("Routine oil change");
            assertThat(entity.getCost()).isEqualByComparingTo(new BigDecimal("75.50"));
            assertThat(entity.getProvider()).isEqualTo("AutoShop");
            assertThat(entity.getPerformedAt()).isEqualTo(performedAt);
            assertThat(entity.getMileageAtService()).isEqualTo(45000L);
            assertThat(entity.getRecordedBy()).isEqualTo("Juan");
        }

        @Test
        @DisplayName("maps null optional fields — description, cost, provider")
        void mapsNullOptionalFields() {
            MaintenanceRecord domain = new MaintenanceRecord(
                    id, vehicleId, alertId, ruleId,
                    "Oil Change", null,
                    null, null,
                    performedAt, 45000L, "Juan");

            MaintenanceRecordJpaEntity entity =
                    MaintenanceRecordPersistenceMapper.toJpaEntity(domain);

            assertThat(entity.getDescription()).isNull();
            assertThat(entity.getCost()).isNull();
            assertThat(entity.getProvider()).isNull();
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps all JPA entity fields to domain correctly")
        void mapsAllFields() {
            MaintenanceRecordJpaEntity entity = new MaintenanceRecordJpaEntity(
                    id, vehicleId, alertId, ruleId,
                    "Tire Rotation", "Rotate all tires",
                    new BigDecimal("30.00"), "TireShop",
                    performedAt, 60000L, "Pedro");

            MaintenanceRecord domain = MaintenanceRecordPersistenceMapper.toDomain(entity);

            assertThat(domain.getId()).isEqualTo(id);
            assertThat(domain.getVehicleId()).isEqualTo(vehicleId);
            assertThat(domain.getAlertId()).isEqualTo(alertId);
            assertThat(domain.getRuleId()).isEqualTo(ruleId);
            assertThat(domain.getServiceType()).isEqualTo("Tire Rotation");
            assertThat(domain.getDescription()).isEqualTo("Rotate all tires");
            assertThat(domain.getCost()).isEqualByComparingTo(new BigDecimal("30.00"));
            assertThat(domain.getProvider()).isEqualTo("TireShop");
            assertThat(domain.getPerformedAt()).isEqualTo(performedAt);
            assertThat(domain.getMileageAtService()).isEqualTo(60000L);
            assertThat(domain.getRecordedBy()).isEqualTo("Pedro");
        }
    }

    @Nested
    @DisplayName("roundtrip")
    class Roundtrip {

        @Test
        @DisplayName("domain → JPA → domain preserves all fields")
        void domainToJpaToDomain() {
            MaintenanceRecord original = new MaintenanceRecord(
                    id, vehicleId, alertId, ruleId,
                    "Brake Inspection", "Full brake check",
                    new BigDecimal("120.00"), "BrakeShop",
                    performedAt, 80000L, "Maria");

            MaintenanceRecord result = MaintenanceRecordPersistenceMapper.toDomain(
                    MaintenanceRecordPersistenceMapper.toJpaEntity(original));

            assertThat(result.getId()).isEqualTo(original.getId());
            assertThat(result.getVehicleId()).isEqualTo(original.getVehicleId());
            assertThat(result.getAlertId()).isEqualTo(original.getAlertId());
            assertThat(result.getRuleId()).isEqualTo(original.getRuleId());
            assertThat(result.getServiceType()).isEqualTo(original.getServiceType());
            assertThat(result.getDescription()).isEqualTo(original.getDescription());
            assertThat(result.getCost()).isEqualByComparingTo(original.getCost());
            assertThat(result.getProvider()).isEqualTo(original.getProvider());
            assertThat(result.getPerformedAt()).isEqualTo(original.getPerformedAt());
            assertThat(result.getMileageAtService()).isEqualTo(original.getMileageAtService());
            assertThat(result.getRecordedBy()).isEqualTo(original.getRecordedBy());
        }

        @Test
        @DisplayName("domain → JPA → domain preserves null optional fields")
        void preservesNullOptionalFields() {
            MaintenanceRecord original = new MaintenanceRecord(
                    id, vehicleId, alertId, ruleId,
                    "Oil Change", null, null, null,
                    performedAt, 45000L, "Juan");

            MaintenanceRecord result = MaintenanceRecordPersistenceMapper.toDomain(
                    MaintenanceRecordPersistenceMapper.toJpaEntity(original));

            assertThat(result.getDescription()).isNull();
            assertThat(result.getCost()).isNull();
            assertThat(result.getProvider()).isNull();
        }
    }
}