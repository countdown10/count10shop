package xyz.tomorrowlearncamp.count10shop.domain.item.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;


public interface ItemElasticRepository extends ElasticsearchRepository<ItemDocument, Long> {

	@Query("{\"match\": {\"itemName.korean\": \"?0\"}}")
	List<ItemDocument> searchByKorean(String keyword);

	@Query("{\"match\": {\"itemName.english\": \"?0\"}}")
	List<ItemDocument> searchByEnglish(String keyword);
}
