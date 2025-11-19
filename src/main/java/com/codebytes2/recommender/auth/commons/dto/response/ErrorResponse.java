package com.codebytes2.recommender.auth.commons.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "DTO for representing an error response")
public class ErrorResponse {

    @Schema(description = "HTTP status code of the error", example = "400")
    private int status;

    @Schema(description = "A brief message describing the error", example = "Bad Request")
    private String message;

    @Schema(description = "A map of validation errors, where the key is the field and the value is the error message",
            example = "{\"email\": \"Formato de Correo no es válido\", \"password\": \"La contraseña No puede estar vacía.\"}")
    private Map<String, String> errors;

    @Schema(description = "Timestamp of when the error occurred", example = "2025-11-18T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String message, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}
