package com.fleetguard.rulesalerts.infrastructure.persistence.mapper;

import com.fleetguard.rulesalerts.domain.model.association.RuleVehicleTypeAssoc;
import com.fleetguard.rulesalerts.infrastructure.persistence.entity.RuleVehicleTypeAssocJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RuleVehicleTypeAssocPersistenceMapper")
class RuleVehicleTypeAssocPersistenceMapperTest {

    private final UUID id = UUID.randomUUID();
    private final UUID ruleId = UUID.randomUUID();
    private final UUID vehicleTypeId = UUID.randomUUID();
    private final LocalDateTime createdAt = LocalDateTime.now();

    @Nested
    @DisplayName("toJpaEntity")
    class ToJpaEntity {

        @Test
        @DisplayName("maps all domain fields to JPA entity correctly")
        void mapsAllFields() {
            RuleVehicleTypeAssoc domain = new RuleVehicleTypeAssoc(
                    id, ruleId, vehicleTypeId, createdAt);

            RuleVehicleTypeAssocJpaEntity entity =
                    RuleVehicleTypeAssocPersistenceMapper.toJpaEntity(domain);

            assertThat(entity.getId()).isEqualTo(id);
            assertThat(entity.getRuleId()).isEqualTo(ruleId);
            assertThat(entity.getVehicleTypeId()).isEqualTo(vehicleTypeId);
            assertThat(entity.getCreatedAt()).isEqualTo(createdAt);
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps all JPA entity fields to domain correctly")
        void mapsAllFields() {
            RuleVehicleTypeAssocJpaEntity entity = new RuleVehicleTypeAssocJpaEntity(
                    id, ruleId, vehicleTypeId, createdAt);

            RuleVehicleTypeAssoc domain =
                    RuleVehicleTypeAssocPersistenceMapper.toDomain(entity);

            assertThat(domain.getId()).isEqualTo(id);
            assertThat(domain.getRuleId()).isEqualTo(ruleId);
            assertThat(domain.getVehicleTypeId()).isEqualTo(vehicleTypeId);
            assertThat(domain.getCreatedAt()).isEqualTo(createdAt);
        }
    }

    @Nested
    @DisplayName("roundtrip")
    class Roundtrip {

        @Test
        @DisplayName("domain → JPA → domain preserves all fields")
        void domainToJpaToDomain() {
            RuleVehicleTypeAssoc original = new RuleVehicleTypeAssoc(
                    id, ruleId, vehicleTypeId, createdAt);

            RuleVehicleTypeAssoc result = RuleVehicleTypeAssocPersistenceMapper.toDomain(
                    RuleVehicleTypeAssocPersistenceMapper.toJpaEntity(original));

            assertThat(result.getId()).isEqualTo(original.getId());
            assertThat(result.getRuleId()).isEqualTo(original.getRuleId());
            assertThat(result.getVehicleTypeId()).isEqualTo(original.getVehicleTypeId());
            assertThat(result.getCreatedAt()).isEqualTo(original.getCreatedAt());
        }
    }
}