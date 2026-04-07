package com.fleetguard.fleet.infrastructure.web.exception;

import com.fleetguard.fleet.domain.exception.*;
import com.fleetguard.fleet.infrastructure.web.dto.response.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("400 Bad Request")
    class BadRequest {

        @Test
        @DisplayName("InvalidMileageException → 400")
        void invalidMileage() {
            InvalidMileageException ex = new InvalidMileageException("Mileage cannot be negative");

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("Mileage cannot be negative");
        }

        @Test
        @DisplayName("InvalidVinException → 400")
        void invalidVin() {
            InvalidVinException ex = new InvalidVinException("VIN must be exactly 17 characters, got: 16");

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("VIN must be exactly 17 characters, got: 16");
        }

        @Test
        @DisplayName("InactiveVehicleException → 400")
        void inactiveVehicle() {
            UUID vehicleId = UUID.randomUUID();
            InactiveVehicleException ex = new InactiveVehicleException(vehicleId);

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("Vehicle " + vehicleId + " is not active");
        }

        @Test
        @DisplayName("MissingRecordedByException → 400")
        void missingRecordedBy() {
            MissingRecordedByException ex = new MissingRecordedByException();

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("Recorded by (driver) is required");
        }

        @Test
        @DisplayName("MethodArgumentNotValidException → 400 with field errors list")
        void validationException() {
            MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("request", "plate", "La placa es obligatoria");

            when(ex.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            ResponseEntity<ErrorResponse> response = handler.handleValidationException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("Validation failed");
            assertThat(response.getBody().getErrors())
                    .hasSize(1)
                    .contains("plate: La placa es obligatoria");
        }
    }

    @Nested
    @DisplayName("404 Not Found")
    class NotFound {

        @Test
        @DisplayName("VehicleNotFoundException → 404")
        void vehicleNotFound() {
            VehicleNotFoundException ex = new VehicleNotFoundException("ABC-1234");

            ResponseEntity<ErrorResponse> response = handler.handleVehicleNotFoundException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(404);
            assertThat(response.getBody().getMessage()).isEqualTo("Vehicle not found with plate: ABC-1234");
        }

        @Test
        @DisplayName("VehicleTypeNotFoundException → 404")
        void vehicleTypeNotFound() {
            UUID typeId = UUID.randomUUID();
            VehicleTypeNotFoundException ex = new VehicleTypeNotFoundException(typeId);

            ResponseEntity<ErrorResponse> response = handler.handleNotFoundException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(404);
            assertThat(response.getBody().getMessage()).isEqualTo("Vehicle type not found with id: " + typeId);
        }
    }

    @Nested
    @DisplayName("409 Conflict")
    class Conflict {

        @Test
        @DisplayName("DuplicatePlateException → 409")
        void duplicatePlate() {
            DuplicatePlateException ex = new DuplicatePlateException("ABC-1234");

            ResponseEntity<ErrorResponse> response = handler.handleDuplicatePlate(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getMessage()).isEqualTo("A vehicle with plate 'ABC-1234' already exists");
        }
    }

    @Nested
    @DisplayName("500 Internal Server Error")
    class InternalServerError {

        @Test
        @DisplayName("Generic Exception → 500")
        void genericException() {
            Exception ex = new Exception("Unexpected error");

            ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(500);
            assertThat(response.getBody().getMessage()).isEqualTo("Internal server error");
        }
    }
}