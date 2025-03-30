package xyz.tomorrowlearncamp.count10shop.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import xyz.tomorrowlearncamp.count10shop.domain.user.enums.UserRole;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = TestSecurityContextFactory.class)
public @interface WithMockAuthUser {
	long userId();
	String email();
	UserRole role();
}
