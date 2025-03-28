package xyz.tomorrowlearncamp.count10shop.domain.item.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;

@Getter
@RequiredArgsConstructor
public class ItemResponseDto {
	private final Long id;
	private final String itemName;
	private final String description;
	private final Long price;
	private final Long quantity;
	private final Status status;

	public static ItemResponseDto of(Item item) {
		return new ItemResponseDto(item.getId(), item.getItemName(), item.getDescription(),
			item.getPrice(), item.getQuantity(), item.getStatus());
	}
}
