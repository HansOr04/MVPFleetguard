package com.fleetguard.fleet.infrastructure.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String message;
    private LocalDateTime timestamp;
    private int status;
    private List<String> errors;

    public ErrorResponse(String message, LocalDateTime timestamp, int status) {
        this(message, timestamp, status, List.of());
    }
}