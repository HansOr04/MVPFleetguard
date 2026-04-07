package com.fleetguard.fleet.infrastructure.persistence.adapter;

import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.infrastructure.persistence.entity.MileageLogJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.mapper.MileageLogMapper;
import com.fleetguard.fleet.infrastructure.persistence.repository.MileageLogJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MileageLogRepositoryAdapter")
class MileageLogRepositoryAdapterTest {

    @Mock
    private MileageLogJpaRepository mileageLogRepository;

    @InjectMocks
    private MileageLogRepositoryAdapter adapter;

    private MileageLog mileageLog;
    private MileageLogJpaEntity mileageLogJpaEntity;

    @BeforeEach
    void setUp() {
        UUID vehicleId = UUID.randomUUID();
        UUID vehicleTypeId = UUID.randomUUID();
        LocalDateTime recordedAt = LocalDateTime.now();

        mileageLog = MileageLog.create(
                vehicleId, vehicleTypeId, "ACTIVE",
                new Mileage(0), new Mileage(1000),
                recordedAt, "driver@fleetguard.com", false);

        mileageLogJpaEntity = MileageLogMapper.toJpaEntity(mileageLog);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("saves and returns mapped domain object")
        void savesAndReturnsDomain() {
            when(mileageLogRepository.save(any(MileageLogJpaEntity.class)))
                    .thenReturn(mileageLogJpaEntity);

            MileageLog result = adapter.save(mileageLog);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(mileageLog.getId());
            assertThat(result.getVehicleId()).isEqualTo(mileageLog.getVehicleId());
            assertThat(result.getMileageValue().getValue()).isEqualTo(1000L);
            assertThat(result.getPreviousMileage().getValue()).isZero();
            assertThat(result.getKmTraveled()).isEqualTo(1000L);
            assertThat(result.getRecordedBy()).isEqualTo("driver@fleetguard.com");
            verify(mileageLogRepository, times(1)).save(any(MileageLogJpaEntity.class));
        }

        @Test
        @DisplayName("calls repository save exactly once")
        void callsRepositoryOnce() {
            when(mileageLogRepository.save(any(MileageLogJpaEntity.class)))
                    .thenReturn(mileageLogJpaEntity);

            adapter.save(mileageLog);

            verify(mileageLogRepository, times(1)).save(any(MileageLogJpaEntity.class));
            verifyNoMoreInteractions(mileageLogRepository);
        }
    }
}