package com.codebytes2.recommender.tournament.controller;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// Note: This is a copy from the rating.controller package to avoid cross-test dependencies.
// It requires a @WithUserEntity annotation to be defined inside TournamentControllerTest.
public class WithUserEntitySecurityContextFactory implements WithSecurityContextFactory<TournamentControllerTest.WithUserEntity> {

    @Override
    public SecurityContext createSecurityContext(TournamentControllerTest.WithUserEntity withUserEntity) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserEntity principal = new UserEntity();
        principal.setId(UUID.randomUUID());
        principal.setUsername(withUserEntity.username());
        principal.setEmail(withUserEntity.username() + "@example.com");
        principal.setPassword("password");

        Set<UserRole> roles = Arrays.stream(withUserEntity.roles())
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
        principal.setRoles(roles);

        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, "password", authorities);

        context.setAuthentication(authentication);
        return context;
    }
}
