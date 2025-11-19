package com.codebytes2.recommender.auth.commons.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(description = "DTO for user login request")
public class LoginRequest {
    @Schema(description = "User's email address", example = "user@example.com")
    @Email(message = "Formato de Correo no es válido")
    @NotBlank(message = "Este Campo No Puede estar vacío")
    private String email;

    @Schema(description = "User's password", example = "password123")
    @NotBlank(message = "La contraseña No puede estar vacía.")
    private String password;
}
