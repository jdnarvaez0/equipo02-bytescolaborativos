package com.codebytes2.recommender.auth.service;

import com.codebytes2.recommender.auth.commons.dto.request.LoginRequest;
import com.codebytes2.recommender.auth.commons.dto.request.UserEntityRequest;
import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.commons.dto.response.UserResponse;
import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import com.codebytes2.recommender.auth.exceptions.DuplicateEmailException;
import com.codebytes2.recommender.auth.repository.UserEntityRepository;
import com.codebytes2.recommender.auth.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntityRequest userEntityRequest;
    private UserEntity userEntity;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userEntityRequest = new UserEntityRequest("testuser", "test@test.com", "password");
        userId = UUID.randomUUID();
        userEntity = new UserEntity();
        userEntity.setId(userId);
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@test.com");
        userEntity.setPassword("encodedPassword");
        userEntity.setRoles(Set.of(UserRole.ADMIN));
    }

    @Test
    void createUser_Success() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userEntityRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserResponse userResponse = authService.createUser(userEntityRequest);

        assertNotNull(userResponse);
        assertEquals(userEntity.getId(), userResponse.getId());
        assertEquals(userEntity.getUsername(), userResponse.getUsername());
        assertEquals(userEntity.getEmail(), userResponse.getEmail());
    }

    @Test
    void createUser_DuplicateEmail() {
        when(userEntityRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));

        assertThrows(DuplicateEmailException.class, () -> {
            authService.createUser(userEntityRequest);
        });
    }

    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest("test@test.com", "password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(userEntity, null);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(anyString(), any())).thenReturn(new TokenResponse("test-token"));

        TokenResponse tokenResponse = authService.login(loginRequest);

        assertNotNull(tokenResponse);
        assertEquals("test-token", tokenResponse.getAccessToken());
    }

    @Test
    void login_InvalidCredentials_ThrowsBadCredentialsException() {
        LoginRequest loginRequest = new LoginRequest("test@test.com", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }
}