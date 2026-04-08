package com.fleetguard.rulesalerts.infrastructure.client;

import com.fleetguard.rulesalerts.application.ports.out.VehicleQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class VehicleQueryAdapter implements VehicleQueryPort {

    private final RestTemplate restTemplate;

    @Value("${fleet.service.url}")
    private String fleetServiceUrl;

    @Override
    public Optional<UUID> findVehicleIdByPlate(String plate) {
        try {
            VehicleClientResponse response = restTemplate.getForObject(
                    fleetServiceUrl + "/api/vehicles/" + plate,
                    VehicleClientResponse.class
            );
            if (response == null || response.id() == null) {
                return Optional.empty();
            }
            return Optional.of(response.id());
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Vehicle not found in fleet-service for plate: {}", plate);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error querying fleet-service for plate: {}", plate, e);
            return Optional.empty();
        }
    }

    public record VehicleClientResponse(UUID id, String plate, String status) {
    }
}