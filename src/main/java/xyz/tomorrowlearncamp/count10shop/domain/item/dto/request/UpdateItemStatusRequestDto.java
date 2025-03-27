package xyz.tomorrowlearncamp.count10shop.domain.item.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateItemStatusRequestDto {
	@NotBlank
	private final String status;
}
