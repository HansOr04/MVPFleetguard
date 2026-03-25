package com.fleetguard.rulesalerts.infrastructure.web.exception;

import jakarta.persistence.EntityNotFoundException;

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
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -----------------------------------------------------------------------
    // 400 – Validación de campos
    // -----------------------------------------------------------------------

    /**
     * Captura errores de validación de Bean Validation (@Valid / @Validated).
     * Devuelve la lista de mensajes de todos los campos inválidos.
     *
     * Respuesta:
     * {
     *   "status": 400,
     *   "errors": ["El nombre de la regla es obligatorio y no puede estar vacío.", ...]
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
                "status", HttpStatus.BAD_REQUEST.value(),
                "errors", errors
        );
    }

    // -----------------------------------------------------------------------
    // 404 – Recurso no encontrado
    // -----------------------------------------------------------------------

    /**
     * Captura EntityNotFoundException lanzada desde la capa de dominio/aplicación.
     *
     * Respuesta:
     * {
     *   "status": 404,
     *   "errors": ["Regla de mantenimiento no encontrada con id: 42"]
     * }
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Map<String, Object> handleEntityNotFound(EntityNotFoundException ex) {
        return Map.of(
                "status", HttpStatus.NOT_FOUND.value(),
                "errors", List.of(ex.getMessage())
        );
    }

    // -----------------------------------------------------------------------
    // 409 – Conflicto de integridad de datos (duplicados, FK violadas, etc.)
    // -----------------------------------------------------------------------

    /**
     * Captura DataIntegrityViolationException de Spring Data / JPA.
     * Generalmente ocurre cuando se intenta insertar un registro duplicado
     * o se viola una restricción de clave foránea.
     *
     * Respuesta:
     * {
     *   "status": 409,
     *   "errors": ["Ya existe un registro con los datos proporcionados. Verifique los campos únicos."]
     * }
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Map<String, Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return Map.of(
                "status", HttpStatus.CONFLICT.value(),
                "errors", List.of(
                        "Ya existe un registro con los datos proporcionados. Verifique los campos únicos."
                )
        );
    }
}
