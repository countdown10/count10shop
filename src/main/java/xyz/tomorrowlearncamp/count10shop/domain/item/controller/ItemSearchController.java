package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemSearchService;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ItemSearchController {
	private final ItemSearchService itemSearchService;
	private final ItemElasticRepository itemElasticRepository;

	@GetMapping("/v1/search")
	public ResponseEntity<ItemDocumentPageResponseDto> search(
		@NotBlank @RequestParam String keyword,
		@RequestParam (defaultValue = "1") int page,
		@RequestParam (defaultValue = "10") int size
	) {
		return ResponseEntity.ok(itemSearchService.searchByKeyword(keyword, page, size));
	}

	@PostMapping("/v1/testdb2")
	public void testdb() {
		List<ItemDocument> items = new ArrayList<>();

		for (int i = 1; i <= 100_000; i++) {
			ItemDocument item = ItemDocument.builder()
				.id((long) i)
				.itemName("Item " + i)
				.category(Category.valueOf("ELECTRONICS"))
				.description("Description " + i)
				.price((long) (i * 10))
				.quantity((long) (i % 100))
				.status(Status.valueOf("SALE"))
				.build();
			items.add(item);

			// Batch insert: 1000개씩 저장 후 리스트 초기화
			if (i % 1000 == 0) {
				itemElasticRepository.saveAll(items);
				items.clear();
			}
		}
	}
}
