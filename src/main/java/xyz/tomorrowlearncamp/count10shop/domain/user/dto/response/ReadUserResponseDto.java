package xyz.tomorrowlearncamp.count10shop.domain.user.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReadUserResponseDto {
	private final Long id;
	private final String email;
	private final LocalDateTime createdAt;
	private final LocalDateTime modifiedAt;

	@Builder
	public ReadUserResponseDto(Long id, String email, LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.id = id;
		this.email = email;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}
}
