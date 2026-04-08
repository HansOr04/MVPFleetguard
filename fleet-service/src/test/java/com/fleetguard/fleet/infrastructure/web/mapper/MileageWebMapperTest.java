package com.fleetguard.fleet.infrastructure.web.mapper;

import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageCommand;
import com.fleetguard.fleet.application.ports.in.RegisterMileageUseCase.RegisterMileageResponse;
import com.fleetguard.fleet.infrastructure.web.dto.request.RegisterMileageRequest;
import com.fleetguard.fleet.infrastructure.web.dto.response.MileageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MileageWebMapper")
class MileageWebMapperTest {

    private MileageWebMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MileageWebMapper();
    }

    @Nested
    @DisplayName("toCommand")
    class ToCommand {

        @Test
        @DisplayName("maps plate, mileageValue and recordedBy correctly")
        void mapsToCommandCorrectly() {
            RegisterMileageRequest request = new RegisterMileageRequest(5000L, "driver@fleetguard.com");

            RegisterMileageCommand command = mapper.toCommand("ABC-1234", request);

            assertThat(command.plate()).isEqualTo("ABC-1234");
            assertThat(command.mileageValue()).isEqualTo(5000L);
            assertThat(command.recordedBy()).isEqualTo("driver@fleetguard.com");
        }

        @Test
        @DisplayName("plate from path variable takes precedence over request body")
        void usesPlateFromPathVariable() {
            RegisterMileageRequest request = new RegisterMileageRequest(1000L, "Juan");

            RegisterMileageCommand command = mapper.toCommand("XYZ-9999", request);

            assertThat(command.plate()).isEqualTo("XYZ-9999");
        }
    }

    @Nested
    @DisplayName("toResponse")
    class ToResponse {

        @Test
        @DisplayName("maps all fields from RegisterMileageResponse to MileageResponse correctly")
        void mapsToResponseCorrectly() {
            UUID mileageLogId = UUID.randomUUID();
            UUID vehicleId = UUID.randomUUID();
            UUID alertId = UUID.randomUUID();
            LocalDateTime recordedAt = LocalDateTime.now();

            RegisterMileageResponse serviceResponse = new RegisterMileageResponse(
                    mileageLogId, vehicleId, "ABC-1234",
                    0L, 1000L, 1000L, 1000L,
                    "driver@fleetguard.com", recordedAt,
                    false, alertId);

            MileageResponse response = mapper.toResponse(serviceResponse);

            assertThat(response.getMileageLogId()).isEqualTo(mileageLogId);
            assertThat(response.getVehicleId()).isEqualTo(vehicleId);
            assertThat(response.getPlate()).isEqualTo("ABC-1234");
            assertThat(response.getPreviousMileage()).isZero();
            assertThat(response.getMileageValue()).isEqualTo(1000L);
            assertThat(response.getKmTraveled()).isEqualTo(1000L);
            assertThat(response.getCurrentMileage()).isEqualTo(1000L);
            assertThat(response.getRecordedBy()).isEqualTo("driver@fleetguard.com");
            assertThat(response.getRecordedAt()).isEqualTo(recordedAt);
            assertThat(response.isExcessiveIncrement()).isFalse();
            assertThat(response.getAlertId()).isEqualTo(alertId);
        }

        @Test
        @DisplayName("maps excessiveIncrement as true when flagged")
        void mapsExcessiveIncrementTrue() {
            RegisterMileageResponse serviceResponse = new RegisterMileageResponse(
                    UUID.randomUUID(), UUID.randomUUID(), "ABC-1234",
                    0L, 3000L, 3000L, 3000L,
                    "driver@fleetguard.com", LocalDateTime.now(),
                    true, null);

            MileageResponse response = mapper.toResponse(serviceResponse);

            assertThat(response.isExcessiveIncrement()).isTrue();
            assertThat(response.getAlertId()).isNull();
        }
    }
}