package com.fleetguard.rulesalerts.infrastructure.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VehicleQueryAdapter")
class VehicleQueryAdapterTest {

    @Mock private RestTemplate restTemplate;

    @InjectMocks
    private VehicleQueryAdapter adapter;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "fleetServiceUrl", "http://fleet-service:8080");
    }

    @Nested
    @DisplayName("findVehicleIdByPlate")
    class FindVehicleIdByPlate {

        @Test
        @DisplayName("returns vehicleId when fleet-service responds with valid data")
        void returnsVehicleId() {
            UUID vehicleId = UUID.randomUUID();
            VehicleQueryAdapter.VehicleClientResponse response =
                    new VehicleQueryAdapter.VehicleClientResponse(vehicleId, "ABC-1234", "ACTIVE");

            when(restTemplate.getForObject(
                    "http://fleet-service:8080/api/vehicles/ABC-1234",
                    VehicleQueryAdapter.VehicleClientResponse.class))
                    .thenReturn(response);

            Optional<UUID> result = adapter.findVehicleIdByPlate("ABC-1234");

            assertThat(result).contains(vehicleId);
        }

        @Test
        @DisplayName("returns empty when response is null")
        void returnsEmptyWhenResponseNull() {
            when(restTemplate.getForObject(anyString(), any()))
                    .thenReturn(null);

            Optional<UUID> result = adapter.findVehicleIdByPlate("ABC-1234");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty when response id is null")
        void returnsEmptyWhenIdNull() {
            VehicleQueryAdapter.VehicleClientResponse response =
                    new VehicleQueryAdapter.VehicleClientResponse(null, "ABC-1234", "ACTIVE");

            when(restTemplate.getForObject(anyString(), any()))
                    .thenReturn(response);

            Optional<UUID> result = adapter.findVehicleIdByPlate("ABC-1234");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty on 404 NotFound — vehicle not in fleet-service")
        void returnsEmptyOn404() {
            when(restTemplate.getForObject(anyString(), any()))
                    .thenThrow(HttpClientErrorException.NotFound.class);

            Optional<UUID> result = adapter.findVehicleIdByPlate("ZZZ-000");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("returns empty on generic exception — does not propagate")
        void returnsEmptyOnGenericException() {
            when(restTemplate.getForObject(anyString(), any()))
                    .thenThrow(new RuntimeException("connection timeout"));

            Optional<UUID> result = adapter.findVehicleIdByPlate("ABC-1234");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("builds correct URL with plate")
        void buildsCorrectUrl() {
            when(restTemplate.getForObject(anyString(), any())).thenReturn(null);

            adapter.findVehicleIdByPlate("XYZ-9999");

            verify(restTemplate).getForObject(
                    "http://fleet-service:8080/api/vehicles/XYZ-9999",
                    VehicleQueryAdapter.VehicleClientResponse.class);
        }
    }
}