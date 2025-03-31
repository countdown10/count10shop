package xyz.tomorrowlearncamp.count10shop.domain.item.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.CreateItemRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemInfoRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.request.UpdateItemStatusRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final ItemElasticRepository itemElasticRepository;

    @GetMapping("/v1/items")
    public ResponseEntity<Page<ItemListResponseDto>> findAll(
        // todo: page 객체로 입력받기
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String category
    ) {
        return ResponseEntity.ok(itemService.findAll(page, size, category));
    }

    @GetMapping("/v2/items")
    public ResponseEntity<ItemPageResponseDto> RdmsSearch(
        // todo: page 객체로 입력받기
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam String keyword
    ) {
        return ResponseEntity.ok(itemService.nameOrCategoryOrDescriptionPage(page, size, keyword));
    }

    @GetMapping("/v1/items/{itemId}")
    public ResponseEntity<ItemResponseDto> findById(
        @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(itemService.findById(itemId));
    }

    @PostMapping("/v1/items")
    public ResponseEntity<Void> saveItem(
        @Valid @RequestBody CreateItemRequestDto dto
    ) {
        itemService.saveItem(dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(), dto.getQuantity(), dto.getStatus());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/v1/items/info/{itemId}")
    public ResponseEntity<Void> updateItemInfo(
        @PathVariable Long itemId,
        @Valid @RequestBody UpdateItemInfoRequestDto dto
    ) {
        itemService.updateItemInfo(itemId, dto.getItemName(), dto.getCategory(), dto.getDescription(), dto.getPrice(), dto.getQuantity());
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

    @PostMapping("/v1/testdb")
    public void testdb() {
        List<Item> items = new ArrayList<>();

        for (int i = 1; i <= 100_000; i++) {
            Item item = Item.builder()
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
                itemRepository.saveAll(items);
                itemRepository.flush(); // 즉시 DB 반영

                items.clear();
            }
        }
    }
}
