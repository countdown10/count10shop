package xyz.tomorrowlearncamp.count10shop.domain.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;

@Getter
@RequiredArgsConstructor
public class ItemListResponseDto {
	private final Long id;
	private final String itemName;
	private final Category category;
	private final Long price;

	public static ItemListResponseDto of(Item item) {
		return new ItemListResponseDto(item.getId(), item.getItemName(), item.getCategory(), item.getPrice());
	}
}
