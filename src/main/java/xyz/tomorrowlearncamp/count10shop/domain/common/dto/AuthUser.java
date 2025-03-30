package xyz.tomorrowlearncamp.count10shop.domain.common.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import xyz.tomorrowlearncamp.count10shop.domain.user.enums.UserRole;

@Getter
public class AuthUser {
	private final Long id;
	private final String email;
	private final Collection<? extends GrantedAuthority> authorities;

	public AuthUser(Long id, String email, UserRole role) {
		this.id = id;
		this.email = email;
		this.authorities = List.of(new SimpleGrantedAuthority(role.name()));
	}
}
