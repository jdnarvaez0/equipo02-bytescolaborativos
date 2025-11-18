package com.codebytes2.recommender.product.controller;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithUserEntitySecurityContextFactory.class)
public @interface WithUserEntity {
    String username() default "testuser";
    String[] roles() default {"PLAYER"};
}
