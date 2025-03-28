package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemSearchService;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ItemSearchController {
	private final ItemSearchService itemSearchService;

	@GetMapping("/v1/search")
	public ResponseEntity<ItemDocumentPageResponseDto> search(
		@RequestParam String keyword,
		@RequestParam (defaultValue = "1") int page,
		@RequestParam (defaultValue = "10") int size
	) {
		return ResponseEntity.ok(itemSearchService.searchByKeyword(keyword, page, size));
	}
}
