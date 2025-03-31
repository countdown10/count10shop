package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;
import xyz.tomorrowlearncamp.count10shop.domain.popular.service.PopularItemService;

@Service
@RequiredArgsConstructor
public class ItemService {
	private final ItemRepository itemRepository;
	private final PopularItemService popularItemService;
	private final ItemElasticRepository itemElasticRepository;

	public Page<ItemListResponseDto> findAll(int page, int size, String category) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Category cat = category != null ? Category.valueOf(category.toUpperCase()) : null;

		return itemRepository.findByCategory(cat, pageable)
			.map(ItemListResponseDto::of);
	}

	public ItemPageResponseDto nameOrCategoryOrDescriptionPage(int page, int size, String keyword) {
		Pageable pageable = PageRequest.of(page - 1, size);

		List<Item> itemList = itemRepository.searchByItemNameOrCategoryOrDescription(keyword);
		return paging(itemList, pageable);
	}

	@Transactional
	public ItemResponseDto findById(Long id, Long userId) {
		Item findItem = itemRepository.findByIdOrElseThrow(id);

		popularItemService.updateViews(findItem, userId);

		return ItemResponseDto.of(findItem);
	}

	public Item findItemByIdOrElseThrow(Long id) {
		return itemRepository.findByIdOrElseThrow(id);
	}

	@Transactional
	public void saveItem(String itemName, String category, String description, Long price, Long quantity,
		String status) {
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
	public void updateItemInfo(Long id, String itemName, String category, String description, Long price,
		Long quantity) {
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

	private ItemPageResponseDto paging(List<Item> itemList, Pageable pageable) {
		int totalElements = itemList.size();  //전부 몇개인지
		int start = (int) pageable.getOffset(); //시작 번호
		int end = Math.min((start + pageable.getPageSize()), totalElements);    //끝 번호
		List<Item> pageContent = itemList.subList(start, end); //itemList 에서 start 인덱스부터 end 인덱스까지의 부분 리스트를 반환.
		PageImpl<Item> itemPage = new PageImpl<>(pageContent, pageable, totalElements);	//페이징 처리
		int currentPage = itemPage.getNumber() + 1;    //현재 페이지번호
		int totalPages = itemPage.getTotalPages();     //총 페이지 번호

		List<ItemListResponseDto> items = itemPage
			.stream()
			.map(ItemListResponseDto::of)
			.toList(); //itemPage 객체를List<ItemDocumentResponseDto> 객체로 변환

		return new ItemPageResponseDto(items, currentPage, totalPages, totalElements);
	}
}
