package com.codebytes2.recommender.auth.commons.dto.request;

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
public class LoginRequest {
    @Email(message = "Formato de Correo no es válido")
    @NotBlank(message = "Este Campo No Puede estar vacío")
    private String email;

    @NotBlank(message = "La contraseña No puede estar vacía.")
    private String password;
}
