package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.rule.MaintenanceRule;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.MaintenanceRuleJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MaintenanceRulePersistenceMapper")
class MaintenanceRulePersistenceMapperTest {

    private final UUID id = UUID.randomUUID();
    private final LocalDateTime now = LocalDateTime.now();

    @Nested
    @DisplayName("toJpaEntity")
    class ToJpaEntity {

        @Test
        @DisplayName("maps all domain fields to JPA entity correctly")
        void mapsAllFields() {
            MaintenanceRule domain = new MaintenanceRule(
                    id, "Oil Change", "OIL",
                    5000, 500, "ACTIVE", now, now);

            MaintenanceRuleJpaEntity entity = MaintenanceRulePersistenceMapper.toJpaEntity(domain);

            assertThat(entity.getId()).isEqualTo(id);
            assertThat(entity.getName()).isEqualTo("Oil Change");
            assertThat(entity.getMaintenanceType()).isEqualTo("OIL");
            assertThat(entity.getIntervalKm()).isEqualTo(5000);
            assertThat(entity.getWarningThresholdKm()).isEqualTo(500);
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
            assertThat(entity.getCreatedAt()).isEqualTo(now);
            assertThat(entity.getUpdatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps all JPA entity fields to domain correctly")
        void mapsAllFields() {
            MaintenanceRuleJpaEntity entity = new MaintenanceRuleJpaEntity(
                    id, "Tire Rotation", "TIRE",
                    10000, 1000, "ACTIVE", now, now);

            MaintenanceRule domain = MaintenanceRulePersistenceMapper.toDomain(entity);

            assertThat(domain.getId()).isEqualTo(id);
            assertThat(domain.getName()).isEqualTo("Tire Rotation");
            assertThat(domain.getMaintenanceType()).isEqualTo("TIRE");
            assertThat(domain.getIntervalKm()).isEqualTo(10000);
            assertThat(domain.getWarningThresholdKm()).isEqualTo(1000);
            assertThat(domain.getStatus()).isEqualTo("ACTIVE");
            assertThat(domain.getCreatedAt()).isEqualTo(now);
            assertThat(domain.getUpdatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("roundtrip")
    class Roundtrip {

        @Test
        @DisplayName("domain → JPA → domain preserves all fields")
        void domainToJpaToDomain() {
            MaintenanceRule original = new MaintenanceRule(
                    id, "Brake Inspection", "BRAKE",
                    20000, 2000, "ACTIVE", now, now);

            MaintenanceRule result = MaintenanceRulePersistenceMapper.toDomain(
                    MaintenanceRulePersistenceMapper.toJpaEntity(original));

            assertThat(result.getId()).isEqualTo(original.getId());
            assertThat(result.getName()).isEqualTo(original.getName());
            assertThat(result.getMaintenanceType()).isEqualTo(original.getMaintenanceType());
            assertThat(result.getIntervalKm()).isEqualTo(original.getIntervalKm());
            assertThat(result.getWarningThresholdKm()).isEqualTo(original.getWarningThresholdKm());
            assertThat(result.getStatus()).isEqualTo(original.getStatus());
            assertThat(result.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(result.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
        }
    }
}