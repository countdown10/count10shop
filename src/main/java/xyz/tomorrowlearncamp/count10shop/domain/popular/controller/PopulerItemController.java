package xyz.tomorrowlearncamp.count10shop.domain.popular.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.popular.service.PopularItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PopulerItemController {

	private final PopularItemService popularItemService;

	// 인기 상품 TOP 5 조회
	@GetMapping("/v1/popular")
	public ResponseEntity<Map<String, Long>> getItemRanking() {
		Map<String, Long> topNProductsWithScores = popularItemService.getRanking();
		return ResponseEntity.ok(topNProductsWithScores);
	}

	@Deprecated
	@DeleteMapping("/v1/popular")
	public ResponseEntity<Void> deleteRanking() {
		popularItemService.resetViewCounts();
		return ResponseEntity.ok().build();
	}
}
