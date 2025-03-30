package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import java.util.List;

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

		List<ItemDocument> itemDocuments = itemElasticRepository.searchByItemNameOrCategoryOrDescription(keyword);
		return paging(itemDocuments, pageable);
	}

	// private boolean isKorean(String keyword) {
	// 	return keyword.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*"); // 한글이 포함된 경우 true
	// }

	private ItemDocumentPageResponseDto paging(List<ItemDocument> itemList, Pageable pageable) {
		int totalElements = itemList.size();  //전부 몇개인지
		int start = (int) pageable.getOffset(); //시작 번호
		int end = Math.min((start + pageable.getPageSize()), totalElements);    //끝 번호
		List<ItemDocument> pageContent = itemList.subList(start, end); //itemList 에서 start 인덱스부터 end 인덱스까지의 부분 리스트를 반환.
		PageImpl<ItemDocument> itemPage = new PageImpl<>(pageContent, pageable, totalElements);	//페이징 처리
		int currentPage = itemPage.getNumber() + 1;    //현재 페이지번호
		int totalPages = itemPage.getTotalPages();     //총 페이지 번호

		List<ItemDocumentResponseDto> items = itemPage
			.stream()
			.map(ItemDocumentResponseDto::of)
			.toList(); //itemPage 객체를List<ItemDocumentResponseDto> 객체로 변환

		return new ItemDocumentPageResponseDto(items, currentPage, totalPages, totalElements);
	}
}
