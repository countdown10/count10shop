package xyz.tomorrowlearncamp.count10shop.domain.item.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemElasticRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemSearchService {

	private final ItemElasticRepository itemElasticRepository;

	public List<ItemDocument> searchByKeyword(String keyword) {
		return itemElasticRepository.findByItemName(keyword);
	}
}
