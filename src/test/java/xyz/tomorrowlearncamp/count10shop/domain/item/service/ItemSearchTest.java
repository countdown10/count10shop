package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;

@ExtendWith(MockitoExtension.class)
public class ItemSearchTest {
	@Mock
	private ItemElasticRepository itemElasticRepository;

	@InjectMocks
	private ItemSearchService itemSearchService;

	// @Test
	// @DisplayName("상품이름이 영어 혹은 한국어일 때 검색 테스트")
	// public void testSearchByKeyword_KoreanAndEnglish() {
	// 	// Given
	// 	String koreanKeyword = "상품이름";
	// 	String englishKeyword = "ProductName";
	//
	// 	List<ItemDocument> koreanMockResults = List.of(
	// 		new ItemDocument(1L, koreanKeyword, 15000L, Category.BEAUTY, "미용상품", Status.SALE, 10L),
	// 		new ItemDocument(2L, koreanKeyword, 20000L, Category.OTHER, "기타상품", Status.SALE, 10L)
	// 	);
	//
	// 	List<ItemDocument> englishMockResults = List.of(
	// 		new ItemDocument(3L, englishKeyword, 30000L, Category.ELECTRONICS, "전자제품", Status.SALE, 5L),
	// 		new ItemDocument(4L, englishKeyword, 40000L, Category.OTHER, "가정용품", Status.SALE, 7L)
	// 	);
	//
	// 	given(itemElasticRepository.searchByKorean(koreanKeyword)).willReturn(koreanMockResults);
	// 	given(itemElasticRepository.searchByItemNameOrCategory(englishKeyword)).willReturn(englishMockResults);
	//
	// 	// When (Korean Keyword)
	// 	ItemDocumentPageResponseDto koreanResult = itemSearchService.searchByKeyword(koreanKeyword, 1, 10);
	//
	// 	// When (English Keyword)
	// 	ItemDocumentPageResponseDto englishResult = itemSearchService.searchByKeyword(englishKeyword, 1, 10);
	//
	// 	// Then (Korean Keyword)
	// 	assertEquals(2, koreanResult.getTotalElements());
	// 	assertEquals(1, koreanResult.getCurrentPageNum());
	// 	assertEquals(1, koreanResult.getTotalPages());
	// 	verify(itemElasticRepository, times(1)).searchByKorean(koreanKeyword);
	//
	// 	// Then (English Keyword)
	// 	assertEquals(2, englishResult.getTotalElements());
	// 	assertEquals(1, englishResult.getCurrentPageNum());
	// 	assertEquals(1, englishResult.getTotalPages());
	// 	verify(itemElasticRepository, times(1)).searchByItemNameOrCategory(englishKeyword);
	//
	// 	// Verify no unnecessary calls
	// 	verify(itemElasticRepository, never()).searchByKorean(englishKeyword);
	// 	verify(itemElasticRepository, never()).searchByItemNameOrCategory(koreanKeyword);
	// }
	//
	// @Test
	// @DisplayName("카테고리 검색 일때 테스트")
	// public void testSearchByCategory() {
	// 	// Given
	// 	String keyword = "BEAUTY";
	// 	ItemDocument itemDocument = new ItemDocument(1L, "미용상품", 15000L, Category.BEAUTY, "미용상품", Status.SALE, 10L);
	// 	ItemDocument itemDocument2 = new ItemDocument(2L, "음식", 15000L, Category.FOOD, "맛난음식", Status.SALE, 10L);
	// 	ItemDocument itemDocument3 = new ItemDocument(3L, "BeautyProduct", 30000L, Category.BEAUTY, "미용상품", Status.SALE,
	// 		10L);
	// 	List<ItemDocument> itemList = List.of(itemDocument, itemDocument2, itemDocument3);
	//
	// 	List<ItemDocument> mockResult = itemList.stream()
	// 		.filter(item -> item.getCategory().name().equals(keyword))
	// 		.toList();
	//
	// 	given(itemElasticRepository.searchByItemNameOrCategory(keyword)).willReturn(mockResult);
	//
	// 	// When
	// 	ItemDocumentPageResponseDto result = itemSearchService.searchByKeyword(keyword, 1, 10);
	//
	// 	// Then
	// 	assertEquals(2, result.getTotalElements());
	// 	assertEquals(1, result.getCurrentPageNum());
	// 	assertEquals(1, result.getTotalPages());
	// 	assertEquals("BeautyProduct", result.getItems().get(1).getItemName());
	// 	verify(itemElasticRepository, never()).searchByKorean(keyword);
	// 	verify(itemElasticRepository, times(1)).searchByItemNameOrCategory(keyword);
	// }

	@Test
	@DisplayName("엘라스틱서치를 이용한 상품검색 테스트")
	void testSearchItems() {
		//given
		String keyword = "english";
		String keyword2 = "한글";

		ItemDocument itemDocument = new ItemDocument(1L, keyword2, 15000L, Category.OTHER, "한글상품입니다.", Status.SALE, 10L);
		ItemDocument itemDocument2 = new ItemDocument(2L, keyword, 15000L, Category.FOOD, "영어상품입니다.", Status.SALE, 10L);
		ItemDocument itemDocument3 = new ItemDocument(3L, keyword, 15000L, Category.CLOTHES, "It is beauty clothes.", Status.SALE, 10L);

		List<ItemDocument> itemDocumentList = List.of(itemDocument, itemDocument2, itemDocument3);
		Pageable pageable = PageRequest.of(0, 10);
		PageImpl<ItemDocument> pageImpl = new PageImpl<>(itemDocumentList, pageable, itemDocumentList.size());

		given(itemElasticRepository.searchByItemNameOrCategoryOrDescription(keyword, pageable)).willReturn(pageImpl);

		//when
		ItemDocumentPageResponseDto result = itemSearchService.searchByKeyword(keyword, 1, 10);

		//then
		assertEquals(3, result.getTotalElements());
		assertEquals(1, result.getCurrentPageNum());
		assertEquals(1, result.getTotalPages());
		verify(itemElasticRepository, times(1)).searchByItemNameOrCategoryOrDescription(keyword, pageable);

	}
}
