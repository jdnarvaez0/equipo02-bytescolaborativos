package com.codebytes2.recommender.product.controller;

import com.codebytes2.recommender.auth.commons.models.entity.UserEntity;
import com.codebytes2.recommender.auth.commons.models.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("productWithUserEntitySecurityContextFactory")
public class WithUserEntitySecurityContextFactory implements WithSecurityContextFactory<WithUserEntity> {

    @Override
    public SecurityContext createSecurityContext(WithUserEntity customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Set<UserRole> roles = Arrays.stream(customUser.roles())
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());

        UserEntity principal = new UserEntity();
        principal.setId(UUID.randomUUID());
        principal.setUsername(customUser.username());
        principal.setEmail(customUser.username() + "@example.com");
        principal.setPassword("password");
        principal.setRoles(roles);
        principal.setEnabled(true);
        principal.setAccountNonExpired(true);
        principal.setAccountNonLocked(true);
        principal.setCredentialsNonExpired(true);

        Set<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());

        context.setAuthentication(new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                principal, principal.getPassword(), authorities));

        return context;
    }
}