package xyz.tomorrowlearncamp.count10shop.auth.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignUpUserResponseDto {
	private final long id;
	private final String email;
	private final LocalDateTime createdAt;

	@Builder
	public SignUpUserResponseDto(long id, String name, String email, LocalDateTime createdAt) {
		this.id = id;
		this.email = email;
		this.createdAt = createdAt;
	}
}
