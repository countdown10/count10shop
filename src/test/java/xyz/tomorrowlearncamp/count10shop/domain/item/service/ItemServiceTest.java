package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

	@Mock
	private ItemRepository itemRepository;

	@InjectMocks
	private ItemService itemService;

	@Test
	void 카테고리_없이_조회시_전체_조회() {
		// given
		Item item1 = new Item("옷", Category.CLOTHES, "", 10000L, 20L, Status.SALE);
		Item item2 = new Item("화장품", Category.BEAUTY, "", 5000L, 30L, Status.SALE);
		ReflectionTestUtils.setField(item1, "id", 1L);
		ReflectionTestUtils.setField(item2, "id", 2L);

		ItemListResponseDto dto1 = new ItemListResponseDto(item1.getId(), item1.getItemName(), item1.getCategory(), item1.getPrice());
		ItemListResponseDto dto2 = new ItemListResponseDto(item2.getId(), item2.getItemName(), item2.getCategory(), item2.getPrice());

		Pageable pageable = PageRequest.of(0, 10);

		given(itemRepository.findByCategory(null, pageable)).willReturn(new PageImpl<>(List.of(item1, item2)));

		// when
		Page<ItemListResponseDto> result = itemService.findAll(1, 10, null);

		// then
		assertNotNull(result);
		assertEquals(dto1.getId(), result.getContent().get(0).getId());
		assertEquals(dto1.getItemName(), result.getContent().get(0).getItemName());
		assertEquals(dto1.getCategory(), result.getContent().get(0).getCategory());
		assertEquals(dto1.getPrice(), result.getContent().get(0).getPrice());
		assertEquals(dto2.getId(), result.getContent().get(1).getId());
		assertEquals(dto2.getItemName(), result.getContent().get(1).getItemName());
		assertEquals(dto2.getCategory(), result.getContent().get(1).getCategory());
		assertEquals(dto2.getPrice(), result.getContent().get(1).getPrice());
	}

	@Test
	void 카테고리_조회시_일부_조회() {
		// given
		Item item1 = new Item("옷", Category.CLOTHES, "", 10000L, 20L, Status.SALE);
		Item item2 = new Item("화장품", Category.BEAUTY, "", 5000L, 30L, Status.SALE);
		ReflectionTestUtils.setField(item1, "id", 1L);
		ReflectionTestUtils.setField(item2, "id", 2L);

		ItemListResponseDto dto1 = new ItemListResponseDto(item1.getId(), item1.getItemName(), item1.getCategory(), item1.getPrice());
		ItemListResponseDto dto2 = new ItemListResponseDto(item2.getId(), item2.getItemName(), item2.getCategory(), item2.getPrice());

		Pageable pageable = PageRequest.of(0, 10);

		Category clothes = Category.CLOTHES;
		Category beauty = Category.BEAUTY;

		given(itemRepository.findByCategory(any(), eq(pageable)))
			.willAnswer(invocation -> {
				Category cat = invocation.getArgument(0);  // 첫 번째 인자

				if (cat == null) {
					return new PageImpl<>(List.of(item1, item2));
				} else if (cat.equals(clothes)) {
					return new PageImpl<>(List.of(item1));
				} else if (cat.equals(beauty)) {
					return new PageImpl<>(List.of(item2));
				} else {
					return Page.empty(); // 그 외 값은 빈 리스트
				}
			});

		// when
		Page<ItemListResponseDto> result1 = itemService.findAll(1, 10, "BEAUTY");
		Page<ItemListResponseDto> result2 = itemService.findAll(1, 10, "CLOTHES");



		// then
		// result 1
		assertNotNull(result1);
		assertEquals(dto2.getId(), result1.getContent().get(0).getId());
		assertEquals(dto2.getItemName(), result1.getContent().get(0).getItemName());
		assertEquals(dto2.getCategory(), result1.getContent().get(0).getCategory());
		assertEquals(dto2.getPrice(), result1.getContent().get(0).getPrice());

		assertNotNull(result2);
		assertEquals(dto1.getId(), result2.getContent().get(0).getId());
		assertEquals(dto1.getItemName(), result2.getContent().get(0).getItemName());
		assertEquals(dto1.getCategory(), result2.getContent().get(0).getCategory());
		assertEquals(dto1.getPrice(), result2.getContent().get(0).getPrice());
	}
}