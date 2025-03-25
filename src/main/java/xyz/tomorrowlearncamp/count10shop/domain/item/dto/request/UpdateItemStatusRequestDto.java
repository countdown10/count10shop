package xyz.tomorrowlearncamp.count10shop.domain.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateItemStatusRequestDto {
	private final String status;
}
