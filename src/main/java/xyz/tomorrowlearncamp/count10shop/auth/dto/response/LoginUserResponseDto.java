package xyz.tomorrowlearncamp.count10shop.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginUserResponseDto {
	private final Long id;
	private final String email;

	@Builder
	public LoginUserResponseDto(Long id, String email) {
		this.id = id;
		this.email = email;
	}
}
