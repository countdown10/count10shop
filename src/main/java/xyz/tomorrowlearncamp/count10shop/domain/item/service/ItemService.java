package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final ItemElasticRepository itemElasticRepository;

	public Page<ItemListResponseDto> findAll(int page, int size, String category) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Category cat = category != null ? Category.valueOf(category.toUpperCase()) : null;

		return itemRepository.findByCategory(cat, pageable)
			.map(ItemListResponseDto::of);
	}

	public ItemResponseDto findById(Long id) {
		Item savedItem = itemRepository.findByIdOrElseThrow(id);

		return ItemResponseDto.of(savedItem);
	}

	public Item findItemByIdOrElseThrow(Long id) {
		return itemRepository.findByIdOrElseThrow(id);
	}

	@Transactional
	public void saveItem(String itemName, String category, String description, Long price, Long quantity, String status) {
		Item item = Item.builder()
			.itemName(itemName)
			.category(Category.valueOf(category))
			.description(description)
			.price(price)
			.quantity(quantity)
			.status(Status.valueOf(status))
			.build();

		itemRepository.save(item);
		itemElasticRepository.save(ItemDocument.of(item));
	}

	@Transactional
	public void updateItemInfo(Long id, String itemName, String category, String description, Long price, Long quantity) {
		Item savedItem = itemRepository.findByIdOrElseThrow(id);

		savedItem.updateInfo(itemName, category, description, price, quantity);
		itemElasticRepository.save(ItemDocument.of(savedItem));
	}

	@Transactional
	public void updateItemStatus(Long id, String status) {
		Item savedItem = itemRepository.findByIdOrElseThrow(id);

		savedItem.updateStatus(Status.valueOf(status));
		itemElasticRepository.save(ItemDocument.of(savedItem));
	}
}
