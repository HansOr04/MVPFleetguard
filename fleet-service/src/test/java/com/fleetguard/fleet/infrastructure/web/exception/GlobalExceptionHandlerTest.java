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
            InvalidMileageException ex = new InvalidMileageException("El kilometraje no puede ser negativo");

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("El kilometraje no puede ser negativo");
        }

        @Test
        @DisplayName("InvalidVinException → 400")
        void invalidVin() {
            InvalidVinException ex = new InvalidVinException("El VIN debe tener exactamente 17 caracteres, se obtuvo: 16");

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("El VIN debe tener exactamente 17 caracteres, se obtuvo: 16");
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
            assertThat(response.getBody().getMessage()).isEqualTo("El vehículo " + vehicleId + " no está activo");
        }

        @Test
        @DisplayName("MissingRecordedByException → 400")
        void missingRecordedBy() {
            MissingRecordedByException ex = new MissingRecordedByException();

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(400);
            assertThat(response.getBody().getMessage()).isEqualTo("El campo 'Registrado por' es obligatorio");
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
            assertThat(response.getBody().getMessage()).isEqualTo("Vehículo no encontrado con la placa: ABC-1234");
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
            assertThat(response.getBody().getMessage()).isEqualTo("Tipo de vehículo no encontrado con ID: " + typeId);
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
            assertThat(response.getBody().getMessage()).isEqualTo("Ya existe un vehículo con la placa 'ABC-1234'");
        }

        @Test
        @DisplayName("DuplicateVinException → 409")
        void duplicateVin() {
            DuplicateVinException ex = new DuplicateVinException("1HGCM82633A123456");

            ResponseEntity<ErrorResponse> response = handler.handleDuplicateVin(ex);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getStatus()).isEqualTo(409);
            assertThat(response.getBody().getMessage()).isEqualTo("El VIN '1HGCM82633A123456' ya existe en el sistema");
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