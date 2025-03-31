package xyz.tomorrowlearncamp.count10shop.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import xyz.tomorrowlearncamp.count10shop.domain.user.enums.UserRole;

@Getter
public class LoginUserResponseDto {
	private final Long id;
	private final String email;
	private final UserRole userRole;

	@Builder
	public LoginUserResponseDto(Long id, String email, UserRole userRole) {
		this.id = id;
		this.email = email;
		this.userRole = userRole;
	}
}
