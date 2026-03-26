package com.fleetguard.rulesalerts.infrastructure.web.exception;

import com.fleetguard.rulesalerts.domain.exception.MaintenanceRuleNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

/**
 * Manejador global de excepciones para rules-alerts-service.
 * Centraliza las respuestas de error en formato JSON uniforme.
 *
 * Formatos de respuesta:
 *  400 → { "status": 400, "error": "Validation failed", "errors": [...] }
 *  404 → { "status": 404, "error": "Not found",         "message": "..." }
 *  409 → { "status": 409, "error": "Conflict",          "message": "..." }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String KEY_STATUS  = "status";
    private static final String KEY_ERROR   = "error";
    private static final String KEY_ERRORS  = "errors";
    private static final String KEY_MESSAGE = "message";

    // -----------------------------------------------------------------------
    // 400 – Validación de campos (@Valid / @Validated)
    // -----------------------------------------------------------------------

    /**
     * Captura errores de Bean Validation y devuelve la lista de mensajes de
     * todos los campos inválidos.
     *
     * Ejemplo de respuesta:
     * {
     *   "status": 400,
     *   "error": "Validation failed",
     *   "errors": ["El nombre de la regla es obligatorio y no puede estar vacío.",
     *              "El intervalo en kilómetros debe ser mayor a 0."]
     * }
     */
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

    // -----------------------------------------------------------------------
    // 404 – Recurso de dominio no encontrado
    // -----------------------------------------------------------------------

    /**
     * Captura la excepción de dominio {@link MaintenanceRuleNotFoundException}.
     *
     * Ejemplo de respuesta:
     * {
     *   "status": 404,
     *   "error": "Not found",
     *   "message": "Regla de mantenimiento no encontrada"
     * }
     */
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

    // -----------------------------------------------------------------------
    // 409 – Conflicto de integridad de datos (duplicados, FK violadas, etc.)
    // -----------------------------------------------------------------------

    /**
     * Captura {@link org.springframework.dao.DataIntegrityViolationException}
     * de Spring Data / JPA cuando se viola una restricción única o de FK.
     *
     * Ejemplo de respuesta:
     * {
     *   "status": 409,
     *   "error": "Conflict",
     *   "message": "Ya existe un recurso con esos datos"
     * }
     */
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

