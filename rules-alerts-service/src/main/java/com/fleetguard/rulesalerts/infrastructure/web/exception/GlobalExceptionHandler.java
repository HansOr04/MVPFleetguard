package com.fleetguard.rulesalerts.infrastructure.web.exception;

import com.fleetguard.rulesalerts.domain.exception.AlertNotFoundException;
import com.fleetguard.rulesalerts.domain.exception.DuplicateAssociationException;
import com.fleetguard.rulesalerts.domain.exception.InvalidMaintenanceException;
import com.fleetguard.rulesalerts.domain.exception.MaintenanceRuleNotFoundException;
import com.fleetguard.rulesalerts.domain.exception.VehicleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_STATUS = "status";
    private static final String KEY_ERROR = "error";
    private static final String KEY_ERRORS = "errors";
    private static final String KEY_MESSAGE = "message";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        return Map.of(
                KEY_STATUS, HttpStatus.BAD_REQUEST.value(),
                KEY_ERROR, "Validation failed",
                KEY_ERRORS, errors
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MaintenanceRuleNotFoundException.class)
    public Map<String, Object> handleMaintenanceRuleNotFound(MaintenanceRuleNotFoundException ex) {
        return Map.of(
                KEY_STATUS, HttpStatus.NOT_FOUND.value(),
                KEY_ERROR, "Not found",
                KEY_MESSAGE, ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(AlertNotFoundException.class)
    public Map<String, Object> handleAlertNotFound(AlertNotFoundException ex) {
        return Map.of(
                KEY_STATUS, HttpStatus.NOT_FOUND.value(),
                KEY_ERROR, "Not found",
                KEY_MESSAGE, ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(VehicleNotFoundException.class)
    public Map<String, Object> handleVehicleNotFound(VehicleNotFoundException ex) {
        return Map.of(
                KEY_STATUS, HttpStatus.NOT_FOUND.value(),
                KEY_ERROR, "Not found",
                KEY_MESSAGE, ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateAssociationException.class)
    public Map<String, Object> handleDuplicateAssociation(DuplicateAssociationException ex) {
        return Map.of(
                KEY_STATUS, HttpStatus.CONFLICT.value(),
                KEY_ERROR, "Conflict",
                KEY_MESSAGE, ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return Map.of(
                KEY_STATUS, HttpStatus.CONFLICT.value(),
                KEY_ERROR, "Conflict",
                KEY_MESSAGE, "Ya existe un recurso con esos datos"
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidMaintenanceException.class)
    public Map<String, Object> handleInvalidMaintenance(InvalidMaintenanceException ex) {
        return Map.of(
                KEY_STATUS, HttpStatus.BAD_REQUEST.value(),
                KEY_ERROR, "Bad request",
                KEY_MESSAGE, ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleGenericException(Exception ex) {
        log.error("Unhandled exception in rules-alerts-service", ex);
        return Map.of(
                KEY_STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                KEY_ERROR, "Internal server error",
                KEY_MESSAGE, "Error interno del servidor"
        );
    }
}