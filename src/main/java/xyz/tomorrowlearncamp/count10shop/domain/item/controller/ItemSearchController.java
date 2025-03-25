package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemSearchService;

@RestController
@RequestMapping("api/v1/search")
@RequiredArgsConstructor
public class ItemSearchController {
	private final ItemSearchService itemSearchService;

	@GetMapping
	ResponseEntity<List<ItemDocument>> search(@RequestParam String keyword) {
		return ResponseEntity.ok(itemSearchService.searchByKeyword(keyword));
	}
}
