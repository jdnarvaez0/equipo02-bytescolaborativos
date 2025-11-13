package com.codebytes2.recommender.auth.service
;

import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import io.jsonwebtoken.Claims;

import java.util.Set;

public interface JwtService {

    TokenResponse generateToken (String email, Set<UserRole> roles);

    Claims getClaims(String token);

    boolean isExpired(String token);

    String extractRoleAsString(String token);

    String extractEmail(String token);


}
