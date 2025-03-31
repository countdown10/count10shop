package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.dto.AuthUser;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.CreateItemRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemInfoRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemStatusRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {

	private final ItemService itemService;

	@GetMapping("/v1/items")
	public ResponseEntity<Page<ItemListResponseDto>> findAll(
		// todo: page 객체로 입력받기
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(required = false) String category
	) {
		return ResponseEntity.ok(itemService.findAll(page, size, category));
	}

	@GetMapping("/v1/items/{itemId}")
	public ResponseEntity<ItemResponseDto> findById(
		@AuthenticationPrincipal AuthUser authUser,
		@PathVariable Long itemId
	) {
		return ResponseEntity.ok(itemService.findById(itemId, authUser.getId()));
	}

	@PostMapping("/v1/items")
	public ResponseEntity<Void> saveItem(
		@Valid @RequestBody CreateItemRequestDto dto
	) {
		itemService.saveItem(dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(),
			dto.getQuantity(), dto.getStatus());
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/v1/items/info/{itemId}")
	public ResponseEntity<Void> updateItemInfo(
		@PathVariable Long itemId,
		@Valid @RequestBody UpdateItemInfoRequestDto dto
	) {
		itemService.updateItemInfo(itemId, dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(),
			dto.getQuantity());
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/v1/items/status/{itemId}")
	public ResponseEntity<Void> updateItemStatus(
		@PathVariable Long itemId,
		@Valid @RequestBody UpdateItemStatusRequestDto dto
	) {
		itemService.updateItemStatus(itemId, dto.getStatus());
		return ResponseEntity.ok().build();
	}

}
