package com.codebytes2.recommender.auth.controller;

import com.codebytes2.recommender.auth.commons.dto.request.LoginRequest;
import com.codebytes2.recommender.auth.commons.dto.request.UserEntityRequest;
import com.codebytes2.recommender.auth.commons.dto.response.ErrorResponse;
import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.commons.dto.response.UserResponse;
import com.codebytes2.recommender.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "🔐 Autenticación", description = "Registro e inicio de sesión con JWT")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario y devuelve un token JWT de acceso.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo válido",
                                    value = """
                        {
                          "email": "admin@example.com",
                          "password": "admin123"
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Inicio de sesión exitoso",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenResponse.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                            {
                              "accessToken": "eyJhbGciOiJIUzI1NiJ9.xxxxx"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de entrada inválidos (ej: email malformado)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Email inválido",
                                            value = """
                            {
                              "status": 400,
                              "message": "Error de validación",
                              "errors": {
                                "email": "Formato de Correo no es válido"
                              },
                              "timestamp": "2025-11-17T12:00:00"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales incorrectas (email o contraseña inválidos)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Credenciales inválidas",
                                            value = """
                            {
                              "status": 401,
                              "message": "Credenciales inválidas",
                              "timestamp": "2025-11-17T12:00:00"
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @Operation(
            summary = "Registrar nuevo usuario",
            description = """
                        Crea un nuevo usuario con rol **PLAYER** por defecto.
                        **No es posible registrarse como ADMIN mediante este endpoint.**
                        Los administradores son creados únicamente por el equipo.
                        El correo debe ser único.
                        """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del nuevo usuario",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserEntityRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo válido",
                                    value = """
                        {
                          "username": "nuevoAdmin",
                          "email": "admin2@example.com",
                          "password": "Pass123!"
                        }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Usuario creado exitosamente",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponse.class),
                                    examples = @ExampleObject(
                                            name = "Respuesta exitosa",
                                            value = """
                        {
                          "id": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                          "username": "jugador1",
                          "email": "jugador1@example.com",
                          "roles": ["PLAYER"]
                        }
                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Datos de entrada inválidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Campos faltantes",
                                            value = """
                            {
                              "status": 400,
                              "message": "Error de validación",
                              "errors": {
                                "username": "Nombre de Usuario Obligatorio",
                                "password": "La contraseña no puede estar vacía"
                              },
                              "timestamp": "2025-11-17T12:00:00"
                            }
                            """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "El correo ya está registrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(
                                            name = "Email duplicado",
                                            value = """
                            {
                              "status": 409,
                              "message": "El email ya está registrado",
                              "timestamp": "2025-11-17T12:00:00"
                            }
                            """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserEntityRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.createUser(userRequest));
    }
}