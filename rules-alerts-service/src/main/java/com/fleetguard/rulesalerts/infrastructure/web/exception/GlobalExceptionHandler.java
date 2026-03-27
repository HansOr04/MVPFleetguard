package com.fleetguard.rulesalerts.infrastructure.web.exception;

import com.fleetguard.rulesalerts.domain.exception.DuplicateAssociationException;
import com.fleetguard.rulesalerts.domain.exception.MaintenanceRuleNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_STATUS  = "status";
    private static final String KEY_ERROR   = "error";
    private static final String KEY_ERRORS  = "errors";
    private static final String KEY_MESSAGE = "message";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, Object> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .toList();

        return Map.of(
                KEY_STATUS, HttpStatus.BAD_REQUEST.value(),
                KEY_ERROR,  "Validation failed",
                KEY_ERRORS, errors
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(MaintenanceRuleNotFoundException.class)
    public Map<String, Object> handleMaintenanceRuleNotFound(
            MaintenanceRuleNotFoundException ex) {

        return Map.of(
                KEY_STATUS,  HttpStatus.NOT_FOUND.value(),
                KEY_ERROR,   "Not found",
                KEY_MESSAGE, ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DuplicateAssociationException.class)
    public Map<String, Object> handleDuplicateAssociation(DuplicateAssociationException ex) {
        return Map.of(
                KEY_STATUS,  HttpStatus.CONFLICT.value(),
                KEY_ERROR,   "Conflict",
                KEY_MESSAGE, ex.getMessage()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityViolation(
            DataIntegrityViolationException ex) {

        return Map.of(
                KEY_STATUS,  HttpStatus.CONFLICT.value(),
                KEY_ERROR,   "Conflict",
                KEY_MESSAGE, "Ya existe un recurso con esos datos"
        );
    }
}

