package xyz.tomorrowlearncamp.count10shop.domain.item.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDocumentPageResponseDto{
	private List<ItemDocumentResponseDto> items;
	private int currentPageNum;
	private int totalPages;
	private long totalElements;
}
