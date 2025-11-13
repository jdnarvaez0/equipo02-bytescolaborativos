package com.codebytes2.recommender.auth.controller;

import com.codebytes2.recommender.auth.commons.dto.request.LoginRequest;
import com.codebytes2.recommender.auth.commons.dto.request.UserEntityRequest;
import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<TokenResponse> register(@Valid @RequestBody UserEntityRequest userRequest) {
        return ResponseEntity.ok(authService.createUser(userRequest));
    }
}
