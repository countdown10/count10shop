package xyz.tomorrowlearncamp.count10shop.domain.item.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;

@Getter
@AllArgsConstructor
public class ItemDocumentResponseDto {
	private Long id;

	private String itemName;

	private Long price;

	private Category category;

	private String description;

	private Status status;

	private Long quantity;

	public static ItemDocumentResponseDto of(ItemDocument item) {
		return new ItemDocumentResponseDto(item.getId(), item.getItemName(), item.getPrice(), item.getCategory(),
			item.getDescription(), item.getStatus(), item.getQuantity());
	}
}
