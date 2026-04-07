package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.alert.MaintenanceAlert;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceAlertJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MaintenanceAlertPersistenceMapper")
class MaintenanceAlertPersistenceMapperTest {

    private final UUID id = UUID.randomUUID();
    private final UUID vehicleId = UUID.randomUUID();
    private final UUID vehicleTypeId = UUID.randomUUID();
    private final UUID ruleId = UUID.randomUUID();
    private final LocalDateTime triggeredAt = LocalDateTime.now();

    @Nested
    @DisplayName("toJpaEntity")
    class ToJpaEntity {

        @Test
        @DisplayName("maps all domain fields to JPA entity correctly")
        void mapsAllFields() {
            MaintenanceAlert domain = new MaintenanceAlert(
                    id, vehicleId, vehicleTypeId, ruleId,
                    "PENDING", triggeredAt, 5000L);

            MaintenanceAlertJpaEntity entity = MaintenanceAlertPersistenceMapper.toJpaEntity(domain);

            assertThat(entity.getId()).isEqualTo(id);
            assertThat(entity.getVehicleId()).isEqualTo(vehicleId);
            assertThat(entity.getVehicleTypeId()).isEqualTo(vehicleTypeId);
            assertThat(entity.getRuleId()).isEqualTo(ruleId);
            assertThat(entity.getStatus()).isEqualTo("PENDING");
            assertThat(entity.getTriggeredAt()).isEqualTo(triggeredAt);
            assertThat(entity.getDueAtKm()).isEqualTo(5000L);
        }

        @Test
        @DisplayName("preserves all status values — PENDING, WARNING, OVERDUE, RESOLVED")
        void preservesAllStatuses() {
            for (String status : new String[]{"PENDING", "WARNING", "OVERDUE", "RESOLVED"}) {
                MaintenanceAlert domain = new MaintenanceAlert(
                        id, vehicleId, vehicleTypeId, ruleId,
                        status, triggeredAt, 5000L);

                MaintenanceAlertJpaEntity entity = MaintenanceAlertPersistenceMapper.toJpaEntity(domain);

                assertThat(entity.getStatus()).isEqualTo(status);
            }
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps all JPA entity fields to domain correctly")
        void mapsAllFields() {
            MaintenanceAlertJpaEntity entity = new MaintenanceAlertJpaEntity(
                    id, vehicleId, vehicleTypeId, ruleId,
                    "WARNING", triggeredAt, 10000L);

            MaintenanceAlert domain = MaintenanceAlertPersistenceMapper.toDomain(entity);

            assertThat(domain.getId()).isEqualTo(id);
            assertThat(domain.getVehicleId()).isEqualTo(vehicleId);
            assertThat(domain.getVehicleTypeId()).isEqualTo(vehicleTypeId);
            assertThat(domain.getRuleId()).isEqualTo(ruleId);
            assertThat(domain.getStatus()).isEqualTo("WARNING");
            assertThat(domain.getTriggeredAt()).isEqualTo(triggeredAt);
            assertThat(domain.getDueAtKm()).isEqualTo(10000L);
        }
    }

    @Nested
    @DisplayName("roundtrip")
    class Roundtrip {

        @Test
        @DisplayName("domain → JPA → domain preserves all fields")
        void domainToJpaToDomain() {
            MaintenanceAlert original = new MaintenanceAlert(
                    id, vehicleId, vehicleTypeId, ruleId,
                    "OVERDUE", triggeredAt, 15000L);

            MaintenanceAlert result = MaintenanceAlertPersistenceMapper.toDomain(
                    MaintenanceAlertPersistenceMapper.toJpaEntity(original));

            assertThat(result.getId()).isEqualTo(original.getId());
            assertThat(result.getVehicleId()).isEqualTo(original.getVehicleId());
            assertThat(result.getVehicleTypeId()).isEqualTo(original.getVehicleTypeId());
            assertThat(result.getRuleId()).isEqualTo(original.getRuleId());
            assertThat(result.getStatus()).isEqualTo(original.getStatus());
            assertThat(result.getTriggeredAt()).isEqualTo(original.getTriggeredAt());
            assertThat(result.getDueAtKm()).isEqualTo(original.getDueAtKm());
        }
    }
}