package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;

@ExtendWith(MockitoExtension.class)
public class ItemSearchTest {
	@Mock
	ItemElasticRepository itemElasticRepository;

	@InjectMocks
	ItemSearchService itemSearchService;

	@Test
	@DisplayName("상품이름이 한국어일때 검색 테스트")
	public void testSearchByKeyword_Korean() {
		// Given
		String keyword = "상품이름";
		List<ItemDocument> mockResults = List.of(
			new ItemDocument(1L, keyword, 15000L, Category.BEAUTY, "미용상품", Status.SALE, 10L),
			new ItemDocument(2L, keyword, 20000L, Category.OTHER, "기타상품", Status.SALE, 10L)
		);

		when(itemElasticRepository.searchByKorean(keyword)).thenReturn(mockResults);

		// When
		ItemDocumentPageResponseDto result = itemSearchService.searchByKeyword(keyword, 1, 10);

		// Then
		assertEquals(2, result.getTotalElements());
		assertEquals(1, result.getCurrentPageNum());
		assertEquals(1, result.getTotalPages());
		verify(itemElasticRepository, times(1)).searchByKorean(keyword);
		verify(itemElasticRepository, never()).searchByEnglish(keyword);
	}

	@Test
	@DisplayName("상품이름이 영어일때 검색 테스트")
	public void testSearchByKeyword_English() {
		// Given
		String keyword = "ProductName";
		List<ItemDocument> mockResults = List.of(
			new ItemDocument(1L, keyword, 15000L, Category.BEAUTY, "미용상품", Status.SALE, 10L),
			new ItemDocument(2L, keyword, 20000L, Category.OTHER, "기타상품", Status.SALE, 10L)
		);

		when(itemElasticRepository.searchByEnglish(keyword)).thenReturn(mockResults);

		// When
		ItemDocumentPageResponseDto result = itemSearchService.searchByKeyword(keyword, 1, 10);

		// Then
		assertEquals(2, result.getTotalElements());
		assertEquals(1, result.getCurrentPageNum());
		assertEquals(1, result.getTotalPages());
		verify(itemElasticRepository, never()).searchByKorean(keyword);
		verify(itemElasticRepository, times(1)).searchByEnglish(keyword);
	}



}
