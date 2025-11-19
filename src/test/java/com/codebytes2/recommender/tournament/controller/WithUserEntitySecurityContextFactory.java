package com.codebytes2.recommender.tournament.controller;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("tournamentWithUserEntitySecurityContextFactory")
public class WithUserEntitySecurityContextFactory implements WithSecurityContextFactory<TournamentControllerTest.WithUserEntity> {

    @Override
    public SecurityContext createSecurityContext(TournamentControllerTest.WithUserEntity customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Convert roles from String[] to a Set<UserRole>
        Set<UserRole> roles = Arrays.stream(customUser.roles())
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());

        // Create a UserEntity for the test
        UserEntity principal = new UserEntity();
        principal.setId(UUID.randomUUID());
        principal.setUsername(customUser.username());
        principal.setEmail(customUser.username() + "@example.com");
        principal.setPassword("password"); // Mock password
        principal.setRoles(roles);

        // Create authorities for the SecurityContext
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        // Set the authentication in the context
        context.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), authorities));

        return context;
    }
}