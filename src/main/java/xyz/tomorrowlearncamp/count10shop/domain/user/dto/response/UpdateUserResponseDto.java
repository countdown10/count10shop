package xyz.tomorrowlearncamp.count10shop.domain.user.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UpdateUserResponseDto {
	private final Long id;

	private final String email;

	private final LocalDateTime createdAt;

	private final LocalDateTime updatedAt;

	@Builder
	public UpdateUserResponseDto(Long id, String email, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.email = email;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}
