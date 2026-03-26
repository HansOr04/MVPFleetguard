package com.fleetguard.fleet.infrastructure.persistence.adapter;

import com.fleetguard.fleet.application.ports.out.MileageLogRepositoryPort;
import com.fleetguard.fleet.domain.model.mileage.MileageLog;
import com.fleetguard.fleet.infrastructure.persistence.entity.MileageLogJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.mapper.MileageLogMapper;
import com.fleetguard.fleet.infrastructure.persistence.repository.MileageLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MileageLogRepositoryAdapter implements MileageLogRepositoryPort {

    private final MileageLogJpaRepository mileageLogRepository;

    @Override
    public MileageLog save(MileageLog mileageLog) {
        MileageLogJpaEntity entity = MileageLogMapper.toJpaEntity(mileageLog);
        return MileageLogMapper.toDomain(mileageLogRepository.save(entity));
    }
}
