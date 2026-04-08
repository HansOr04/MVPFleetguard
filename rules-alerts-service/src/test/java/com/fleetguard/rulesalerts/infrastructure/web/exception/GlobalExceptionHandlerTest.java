package com.fleetguard.rulesalerts.infrastructure.web.exception;

import com.fleetguard.rulesalerts.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    @RequestMapping("/test")
    static class TestController {
        @GetMapping("/rule-not-found")
        public void ruleNotFound() {
            throw new MaintenanceRuleNotFoundException("Regla no encontrada con ID: 123");
        }

        @GetMapping("/alert-not-found")
        public void alertNotFound() {
            throw new AlertNotFoundException(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        }

        @GetMapping("/vehicle-not-found")
        public void vehicleNotFound() {
            throw new VehicleNotFoundException("ABC-1234");
        }

        @GetMapping("/duplicate-association")
        public void duplicateAssociation() {
            throw new DuplicateAssociationException("La regla ya está asociada a ese tipo de vehículo");
        }

        @GetMapping("/data-integrity")
        public void dataIntegrity() {
            throw new DataIntegrityViolationException("constraint violation");
        }

        @GetMapping("/invalid-maintenance")
        public void invalidMaintenance() {
            throw new InvalidMaintenanceException("El tipo de servicio es obligatorio");
        }

        @PostMapping("/validate")
        public void validate(@RequestBody @jakarta.validation.Valid ValidatedBody body) {
        }

        record ValidatedBody(
                @jakarta.validation.constraints.NotBlank(message = "El nombre es obligatorio")
                String name
        ) {}
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    @DisplayName("404 Not Found")
    class NotFound {

        @Test
        @DisplayName("MaintenanceRuleNotFoundException → 404 with message")
        void maintenanceRuleNotFound() throws Exception {
            mockMvc.perform(get("/test/rule-not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not found"))
                    .andExpect(jsonPath("$.message").value("Regla no encontrada con ID: 123"));
        }

        @Test
        @DisplayName("AlertNotFoundException → 404 with message")
        void alertNotFound() throws Exception {
            mockMvc.perform(get("/test/alert-not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not found"))
                    .andExpect(jsonPath("$.message", containsString("00000000-0000-0000-0000-000000000001")));
        }

        @Test
        @DisplayName("VehicleNotFoundException → 404 with message")
        void vehicleNotFound() throws Exception {
            mockMvc.perform(get("/test/vehicle-not-found"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not found"))
                    .andExpect(jsonPath("$.message", containsString("ABC-1234")));
        }
    }

    @Nested
    @DisplayName("409 Conflict")
    class Conflict {

        @Test
        @DisplayName("DuplicateAssociationException → 409 with message")
        void duplicateAssociation() throws Exception {
            mockMvc.perform(get("/test/duplicate-association"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message").value("La regla ya está asociada a ese tipo de vehículo"));
        }

        @Test
        @DisplayName("DataIntegrityViolationException → 409 with generic message")
        void dataIntegrityViolation() throws Exception {
            mockMvc.perform(get("/test/data-integrity"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error").value("Conflict"))
                    .andExpect(jsonPath("$.message").value("Ya existe un recurso con esos datos"));
        }
    }

    @Nested
    @DisplayName("400 Bad Request")
    class BadRequest {

        @Test
        @DisplayName("InvalidMaintenanceException → 400 with message")
        void invalidMaintenance() throws Exception {
            mockMvc.perform(get("/test/invalid-maintenance"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Bad request"))
                    .andExpect(jsonPath("$.message").value("El tipo de servicio es obligatorio"));
        }

        @Test
        @DisplayName("MethodArgumentNotValidException → 400 with errors list")
        void validationFailed() throws Exception {
            mockMvc.perform(post("/test/validate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\": \"\"}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error").value("Validation failed"))
                    .andExpect(jsonPath("$.errors", hasItem("El nombre es obligatorio")));
        }
    }
}