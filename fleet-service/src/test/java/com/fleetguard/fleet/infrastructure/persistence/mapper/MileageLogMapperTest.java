package com.fleetguard.fleet.infrastructure.persistence.mapper;

import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.infrastructure.persistence.entity.MileageLogJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MileageLogMapper")
class MileageLogMapperTest {

    @Nested
    @DisplayName("toJpaEntity")
    class ToJpaEntity {

        @Test
        @DisplayName("maps all domain fields to JPA entity correctly")
        void mapsDomainToJpaCorrectly() {
            UUID vehicleId = UUID.randomUUID();
            UUID vehicleTypeId = UUID.randomUUID();
            LocalDateTime recordedAt = LocalDateTime.now();

            MileageLog log = MileageLog.create(
                    vehicleId, vehicleTypeId, "ACTIVE",
                    new Mileage(0), new Mileage(1000),
                    recordedAt, "driver@fleetguard.com", false);

            MileageLogJpaEntity entity = MileageLogMapper.toJpaEntity(log);

            assertThat(entity.getId()).isEqualTo(log.getId());
            assertThat(entity.getVehicleId()).isEqualTo(vehicleId);
            assertThat(entity.getPreviousMileage()).isZero();
            assertThat(entity.getMileageValue()).isEqualTo(1000L);
            assertThat(entity.getKmTraveled()).isEqualTo(1000L);
            assertThat(entity.getRecordedAt()).isEqualTo(recordedAt);
            assertThat(entity.getRecordedBy()).isEqualTo("driver@fleetguard.com");
            assertThat(entity.isExcessiveIncrement()).isFalse();
            assertThat(entity.getVehicleTypeId()).isEqualTo(vehicleTypeId);
        }

        @Test
        @DisplayName("maps excessiveIncrement as true when flagged")
        void mapsExcessiveIncrementTrue() {
            MileageLog log = MileageLog.create(
                    UUID.randomUUID(), UUID.randomUUID(), "ACTIVE",
                    new Mileage(0), new Mileage(3000),
                    LocalDateTime.now(), "driver@fleetguard.com", true);

            MileageLogJpaEntity entity = MileageLogMapper.toJpaEntity(log);

            assertThat(entity.isExcessiveIncrement()).isTrue();
        }
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("maps all JPA entity fields to domain correctly")
        void mapsJpaToDomainCorrectly() {
            UUID id = UUID.randomUUID();
            UUID vehicleId = UUID.randomUUID();
            LocalDateTime recordedAt = LocalDateTime.now();

            MileageLogJpaEntity entity = new MileageLogJpaEntity(
                    id, vehicleId, 0L, 1000L, 1000L,
                    recordedAt, "driver@fleetguard.com", false, null);

            MileageLog log = MileageLogMapper.toDomain(entity);

            assertThat(log.getId()).isEqualTo(id);
            assertThat(log.getVehicleId()).isEqualTo(vehicleId);
            assertThat(log.getPreviousMileage().getValue()).isZero();
            assertThat(log.getMileageValue().getValue()).isEqualTo(1000L);
            assertThat(log.getKmTraveled()).isEqualTo(1000L);
            assertThat(log.getRecordedAt()).isEqualTo(recordedAt);
            assertThat(log.getRecordedBy()).isEqualTo("driver@fleetguard.com");
        }
    }
}