package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentPageResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.dto.response.ItemDocumentResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemSearchService {

	private final ItemElasticRepository itemElasticRepository;

	public ItemDocumentPageResponseDto searchByKeyword(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);

		// if (isKorean(keyword)) {
		// 	List<ItemDocument> itemList = itemElasticRepository.searchByKorean(keyword);
		// 	System.out.println("한글 검색 중");
		// 	return paging(itemList, pageable);
		// }
		//
		// System.out.println("영어 검색 중");
		// List<ItemDocument> itemDocuments = itemElasticRepository.searchByItemNameOrCategory(keyword);
		// return paging(itemDocuments, pageable);

		Page<ItemDocument> itemPage = itemElasticRepository.searchByItemNameOrCategoryOrDescription(keyword, pageable);
		return paging(itemPage);
	}

	// private boolean isKorean(String keyword) {
	// 	return keyword.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"); // 한글이 포함된 경우 true
	// }

	private ItemDocumentPageResponseDto paging(Page<ItemDocument> itemPage) {
		int currentPage = itemPage.getNumber() + 1;    //현재 페이지번호
		int totalPages = itemPage.getTotalPages();     //총 페이지 번호
		long totalElements = itemPage.getTotalElements(); //전체 데이터 개수

		// 응답 DTO로 변환
		List<ItemDocumentResponseDto> items = itemPage
			.stream()
			.map(ItemDocumentResponseDto::of)
			.toList(); // itemPage 객체를 List<ItemDocumentResponseDto> 객체로 변환

		return new ItemDocumentPageResponseDto(items, currentPage, totalPages, totalElements);
	}
}
