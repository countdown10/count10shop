package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.CreateItemRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemInfoRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemStatusRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemListResponseDto>> findAll(
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(itemService.findAll(page, size, category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(itemService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> saveItem(
        @RequestBody CreateItemRequestDto dto
    ) {
        itemService.saveItem(dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(), dto.getQuantity(), dto.getStatus());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateItemInfo(
        @PathVariable Long id,
        @RequestBody UpdateItemInfoRequestDto dto
    ) {
        itemService.updateItemInfo(id, dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(), dto.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateItemStatus(
        @PathVariable Long id,
        @RequestBody UpdateItemStatusRequestDto dto
    ) {
        itemService.updateItemStatus(id, dto.getStatus());
        return ResponseEntity.ok().build();
    }

}
