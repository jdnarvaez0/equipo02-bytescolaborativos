package com.codebytes2.recommender.auth.service;

import com.codebytes2.recommender.auth.commons.dto.request.LoginRequest;
import com.codebytes2.recommender.auth.commons.dto.request.UserEntityRequest;
import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.commons.dto.response.UserResponse;

public interface AuthService {

    UserResponse createUser(UserEntityRequest userEntityRequest);
    TokenResponse login(LoginRequest loginRequest);
}
