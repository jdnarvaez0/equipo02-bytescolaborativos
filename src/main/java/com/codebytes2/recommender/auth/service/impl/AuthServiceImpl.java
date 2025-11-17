package com.codebytes2.recommender.auth.service.impl;

import com.codebytes2.recommender.auth.commons.dto.request.LoginRequest;
import com.codebytes2.recommender.auth.commons.dto.request.UserEntityRequest;
import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.commons.dto.response.UserResponse;
import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import com.codebytes2.recommender.auth.exceptions.DuplicateEmailException;
import com.codebytes2.recommender.auth.repository.UserEntityRepository;
import com.codebytes2.recommender.auth.service.AuthService;
import com.codebytes2.recommender.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserEntityRepository userEntityRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse createUser(UserEntityRequest userEntityRequest) {
        if (userEntityRepository.findByEmail(userEntityRequest.getEmail()).isPresent()) {
            throw new DuplicateEmailException("El email ya está registrado");
        }

        UserEntity userToSave = mapToEntity(userEntityRequest, UserRole.PLAYER);
        UserEntity userCreated = userEntityRepository.save(userToSave);

        return UserResponse.builder()
                .id(userCreated.getId())
                .username(userCreated.getUsername())
                .email(userCreated.getEmail())
                .roles(userCreated.getRoles())
                .build();
    }

    @Override
    public TokenResponse login(LoginRequest loginRequest) {
        log.info("Intentando login para usuario: {}", loginRequest.getEmail());


        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        UserEntity user = (UserEntity) authentication.getPrincipal();
        log.info("Login exitoso para usuario: {}", user.getEmail());

        String rolesAsString = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));

        return jwtService.generateToken(user.getEmail(), user.getRoles());
    }


    private UserEntity mapToEntity(UserEntityRequest userEntityRequest, UserRole role) {
        UserEntity user = new UserEntity();
        user.setUsername(userEntityRequest.getUsername());
        user.setEmail(userEntityRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userEntityRequest.getPassword()));
        user.setRoles(Set.of(role));
        return user;
    }
}