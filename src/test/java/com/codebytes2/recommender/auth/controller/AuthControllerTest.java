package com.codebytes2.recommender.auth.controller;

import com.codebytes2.recommender.auth.commons.dto.request.LoginRequest;
import com.codebytes2.recommender.auth.commons.dto.request.UserEntityRequest;
import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.service.AuthService;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.codebytes2.recommender.auth.service.JwtService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.codebytes2.recommender.auth.exceptions.DuplicateEmailException;
import com.codebytes2.recommender.exceptions.GlobalExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = {AuthController.class, GlobalExceptionHandler.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private TokenResponse tokenResponse;

    @BeforeEach
    void setUp() {
        tokenResponse = TokenResponse.builder().accessToken("test-token").build();
    }

    @Test
    void login() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@test.com", "password");

        when(authService.login(any(LoginRequest.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-token"));
    }

    @Test
    void register() throws Exception {
        UserEntityRequest userRequest = new UserEntityRequest("testuser", "test@test.com", "password");

        when(authService.createUser(any(UserEntityRequest.class))).thenReturn(tokenResponse);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("test-token"));
    }

    @Test
    void register_whenEmailAlreadyExists_shouldReturnConflict() throws Exception {
        UserEntityRequest userRequest = new UserEntityRequest("testuser", "test@test.com", "password");

        when(authService.createUser(any(UserEntityRequest.class)))
                .thenThrow(new DuplicateEmailException("Email already exists"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest("test@test.com", "wrongpassword");

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    void login_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        LoginRequest loginRequest = new LoginRequest("invalid-email", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email", is("Formato de Correo no es válido")));
    }

    @Test
    void register_withBlankUsername_shouldReturnBadRequest() throws Exception {
        UserEntityRequest userRequest = new UserEntityRequest("", "test@test.com", "password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.username", is("Nombre de Usuario Obligatorio")));
    }

    @Test
    void register_withInvalidEmail_shouldReturnBadRequest() throws Exception {
        UserEntityRequest userRequest = new UserEntityRequest("testuser", "invalid-email", "password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email", is("El formato del email no es válido")));
    }

    @Test
    void register_withBlankEmail_shouldReturnBadRequest() throws Exception {
        UserEntityRequest userRequest = new UserEntityRequest("testuser", "", "password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email", is("El email no puede estar vacío")));
    }

    @Test
    void register_withBlankPassword_shouldReturnBadRequest() throws Exception {
        UserEntityRequest userRequest = new UserEntityRequest("testuser", "test@test.com", "");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.password", is("La contraseña no puede estar vacía")));
    }
}
