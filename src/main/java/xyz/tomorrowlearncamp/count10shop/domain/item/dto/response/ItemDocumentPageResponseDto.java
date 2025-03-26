package xyz.tomorrowlearncamp.count10shop.domain.item.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDocumentPageResponseDto{
	List<ItemDocumentResponseDto> items;
	int currentPageNum;
	int totalPages;
	long totalPage;
}
