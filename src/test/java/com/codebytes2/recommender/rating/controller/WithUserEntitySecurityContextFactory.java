package com.codebytes2.recommender.rating.controller;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

@Component("ratingWithUserEntitySecurityContextFactory")
public class WithUserEntitySecurityContextFactory implements WithSecurityContextFactory<RatingControllerTest.WithUserEntity> {

    @Override
    public SecurityContext createSecurityContext(RatingControllerTest.WithUserEntity withUserEntity) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Create UserEntity based on annotation parameters
        UserEntity principal = new UserEntity();
        principal.setId(UUID.randomUUID()); // Assign a random UUID for the test user
        principal.setUsername(withUserEntity.username());
        principal.setEmail(withUserEntity.username() + "@example.com"); // Use username for email for simplicity
        principal.setPassword("password"); // Password is not used in this context, but required by UserDetails

        Set<UserRole> roles = Arrays.stream(withUserEntity.roles())
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
        principal.setRoles(roles);

        // Convert UserRoles to GrantedAuthorities
        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, "password", authorities);

        context.setAuthentication(authentication);
        return context;
    }
}
