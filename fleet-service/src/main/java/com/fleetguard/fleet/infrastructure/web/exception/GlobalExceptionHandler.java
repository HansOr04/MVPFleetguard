package com.fleetguard.fleet.infrastructure.web.exception;

import com.fleetguard.fleet.domain.exception.DuplicatePlateException;
import com.fleetguard.fleet.domain.exception.InactiveVehicleException;
import com.fleetguard.fleet.domain.exception.InvalidMileageException;
import com.fleetguard.fleet.domain.exception.InvalidVinException;
import com.fleetguard.fleet.domain.exception.VehicleTypeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            DuplicatePlateException.class,
            InvalidVinException.class,
            InvalidMileageException.class,
            InactiveVehicleException.class
    })
    public ResponseEntity<Map<String, String>> handleDomainException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(VehicleTypeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fe -> fe.getField(),
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "invalid",
                        (a, b) -> a
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validation failed", "fields", fieldErrors));
    }
}
