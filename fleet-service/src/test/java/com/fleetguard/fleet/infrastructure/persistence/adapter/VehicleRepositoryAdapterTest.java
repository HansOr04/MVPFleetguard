package com.fleetguard.fleet.infrastructure.persistence.adapter;

import com.fleetguard.fleet.domain.model.vehicle.Vehicle;
import com.fleetguard.fleet.domain.model.vehicle.VehicleStatus;
import com.fleetguard.fleet.domain.model.vehicle.VehicleType;
import com.fleetguard.fleet.domain.valueobject.Mileage;
import com.fleetguard.fleet.domain.valueobject.Plate;
import com.fleetguard.fleet.domain.valueobject.Vin;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.entity.VehicleTypeJpaEntity;
import com.fleetguard.fleet.infrastructure.persistence.mapper.VehicleMapper;
import com.fleetguard.fleet.infrastructure.persistence.repository.VehicleJpaRepository;
import com.fleetguard.fleet.infrastructure.persistence.repository.VehicleTypeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleRepositoryAdapter")
class VehicleRepositoryAdapterTest {

    @Mock
    private VehicleJpaRepository vehicleRepository;

    @Mock
    private VehicleTypeJpaRepository vehicleTypeRepository;

    @InjectMocks
    private VehicleRepositoryAdapter vehicleRepositoryAdapter;

    private Vehicle vehicle;
    private VehicleJpaEntity vehicleJpaEntity;
    private final UUID vehicleId = UUID.randomUUID();
    private final UUID typeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        VehicleTypeJpaEntity typeEntity = new VehicleTypeJpaEntity(typeId, "Hatchback", "Small car");

        vehicle = new Vehicle(
                vehicleId,
                new Plate("XYZ-789"),
                "Ford",
                "Focus",
                2021,
                "Gasoline",
                new Vin("3FAHP0HA7AR123456"),
                VehicleStatus.ACTIVE,
                new Mileage(3000),
                new VehicleType(typeId, "Hatchback", "Small car")
        );

        vehicleJpaEntity = new VehicleJpaEntity(
                vehicleId, "XYZ-789", "Ford", "Focus",
                2021, "Gasoline", "3FAHP0HA7AR123456",
                "ACTIVE", 3000, typeEntity);
    }

    @Nested
    @DisplayName("save")
    class Save {

        @Test
        @DisplayName("saves and returns mapped domain object")
        void savesAndReturnsDomain() {
            when(vehicleRepository.save(any(VehicleJpaEntity.class))).thenReturn(vehicleJpaEntity);

            Vehicle result = vehicleRepositoryAdapter.save(vehicle);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(vehicleId);
            assertThat(result.getPlate().getValue()).isEqualTo("XYZ-789");
            verify(vehicleRepository, times(1)).save(any(VehicleJpaEntity.class));
        }
    }

    @Nested
    @DisplayName("findByPlate")
    class FindByPlate {

        @Test
        @DisplayName("returns vehicle when plate exists")
        void returnsVehicleWhenExists() {
            when(vehicleRepository.findByPlate("XYZ-789")).thenReturn(Optional.of(vehicleJpaEntity));

            Optional<Vehicle> result = vehicleRepositoryAdapter.findByPlate("XYZ-789");

            assertThat(result).isPresent();
            assertThat(result.get().getPlate().getValue()).isEqualTo("XYZ-789");
            verify(vehicleRepository, times(1)).findByPlate("XYZ-789");
        }

        @Test
        @DisplayName("returns empty when plate does not exist")
        void returnsEmptyWhenNotFound() {
            when(vehicleRepository.findByPlate("ZZZ-000")).thenReturn(Optional.empty());

            Optional<Vehicle> result = vehicleRepositoryAdapter.findByPlate("ZZZ-000");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsByPlate")
    class ExistsByPlate {

        @Test
        @DisplayName("returns true when plate exists")
        void returnsTrueWhenExists() {
            when(vehicleRepository.existsByPlate("XYZ-789")).thenReturn(true);

            assertThat(vehicleRepositoryAdapter.existsByPlate("XYZ-789")).isTrue();
            verify(vehicleRepository, times(1)).existsByPlate("XYZ-789");
        }

        @Test
        @DisplayName("returns false when plate does not exist")
        void returnsFalseWhenNotExists() {
            when(vehicleRepository.existsByPlate("ZZZ-000")).thenReturn(false);

            assertThat(vehicleRepositoryAdapter.existsByPlate("ZZZ-000")).isFalse();
        }
    }

    @Nested
    @DisplayName("findVehicleTypeById")
    class FindVehicleTypeById {

        @Test
        @DisplayName("returns vehicle type when id exists")
        void returnsVehicleTypeWhenExists() {
            VehicleTypeJpaEntity typeEntity = new VehicleTypeJpaEntity(typeId, "Pickup", "A utility truck");
            when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.of(typeEntity));

            Optional<VehicleType> result = vehicleRepositoryAdapter.findVehicleTypeById(typeId);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(typeId);
            verify(vehicleTypeRepository, times(1)).findById(typeId);
        }

        @Test
        @DisplayName("returns empty when id does not exist")
        void returnsEmptyWhenNotFound() {
            when(vehicleTypeRepository.findById(typeId)).thenReturn(Optional.empty());

            Optional<VehicleType> result = vehicleRepositoryAdapter.findVehicleTypeById(typeId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("returns vehicle when id exists")
        void returnsVehicleWhenExists() {
            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(vehicleJpaEntity));

            Optional<Vehicle> result = vehicleRepositoryAdapter.findById(vehicleId);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(vehicleId);
            verify(vehicleRepository, times(1)).findById(vehicleId);
        }

        @Test
        @DisplayName("returns empty when id does not exist")
        void returnsEmptyWhenNotFound() {
            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

            Optional<Vehicle> result = vehicleRepositoryAdapter.findById(vehicleId);

            assertThat(result).isEmpty();
        }
    }
}