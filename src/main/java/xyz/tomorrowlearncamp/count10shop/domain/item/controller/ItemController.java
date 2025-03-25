package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.CreateItemRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemInfoRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemStatusRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<ItemListResponseDto>> findAll(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(itemService.findAll(page, size, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDto> findById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> saveItem(
        @RequestBody CreateItemRequestDto dto
    ) {
        itemService.saveItem(dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(), dto.getQuantity(), dto.getStatus());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/info/{id}")
    public ResponseEntity<Void> updateItemInfo(
        @PathVariable Long id,
        @RequestBody UpdateItemInfoRequestDto dto
    ) {
        itemService.updateItemInfo(id, dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(), dto.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<Void> updateItemStatus(
        @PathVariable Long id,
        @RequestBody UpdateItemStatusRequestDto dto
    ) {
        itemService.updateItemStatus(id, dto.getStatus());
        return ResponseEntity.ok().build();
    }

}
