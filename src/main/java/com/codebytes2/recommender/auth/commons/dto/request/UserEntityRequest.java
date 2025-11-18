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
@Schema(description = "DTO for user registration request")
public class UserEntityRequest {
    @Schema(description = "Username for the new user", example = "newuser")
    @NotBlank(message = "Nombre de Usuario Obligatorio")
    private String username;

    @Schema(description = "User's email address", example = "newuser@example.com")
    @Email(message = "El formato del email no es válido")
    @NotBlank(message = "El email no puede estar vacío")
    private String email;

    @Schema(description = "Password for the new user", example = "securePassword123")
    @NotBlank(message = "La contraseña no puede estar vacía")
    private String password;

}
