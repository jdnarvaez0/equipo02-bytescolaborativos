package com.codebytes2.recommender.auth.service.impl;

import com.codebytes2.recommender.auth.commons.dto.response.TokenResponse;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import com.codebytes2.recommender.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class JwtServiceImpl implements JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtServiceImpl.class);
    private final SecretKey secretKey;
    private static final long EXPIRATION_TIME = 864_000_000; // 10 días

    public JwtServiceImpl(@Value("${jwt.secret}") String secret) {
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("La clave secreta de JWT debe tener al menos 32 caracteres.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenResponse generateToken(String email, Set<UserRole> roles) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        String normalizedRoles = roles.stream()
                .map(Enum::name)
                .map(r -> r.startsWith("ROLE_") ? r.substring(5) : r)
                .collect(Collectors.joining(","));

        String token = Jwts.builder()
                .subject(email)
                .claim("roles", normalizedRoles) // nota: plural
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();


        return TokenResponse.builder()
                .accessToken(token)
                .build();
    }

    @Override
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Error al parsear JWT: {}, Causa: {}", e.getMessage(),
                    e.getCause() != null ? e.getCause().getMessage() : "N/A");
            throw new IllegalArgumentException("Token JWT inválido o expirado", e);
        }
    }

    @Override
    public boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            // Si hay cualquier error al parsear, se considera expirado/inválido.
            return true;
        }
    }

    @Override
    public String extractRoleAsString(String token) {
        Claims claims = getClaims(token);
        return claims.get("roles", String.class); // usa el mismo nombre que en generateToken
    }

    @Override
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }
}
