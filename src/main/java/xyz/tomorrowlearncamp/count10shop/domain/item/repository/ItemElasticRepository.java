package xyz.tomorrowlearncamp.count10shop.domain.item.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;

public interface ItemElasticRepository extends ElasticsearchRepository<ItemDocument, Long> {

	@Query("{\"match\": {\"itemName.korean\": \"?0\"}}")
	List<ItemDocument> searchByKorean(String keyword);

	@Query("{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"itemName.english\": \"?0\"}}, {\"term\": {\"category\": \"?0\"}}]}}")
	List<ItemDocument> searchByItemNameOrCategory(String keyword);

	@Query("{\"bool\": {\"should\": [{\"match_phrase_prefix\": {\"itemName.mixed\": \"?0\"}}, {\"term\": {\"category\": \"?0\"}}, {\"match_phrase_prefix\": {\"description\": \"?0\"}}]}}")
	Page<ItemDocument> searchByItemNameOrCategoryOrDescription(String keyword, Pageable pageable);


	//?0은 메서드에 전달된 첫 번째 매개변수를 의미합니다. 지금 같은 경우는 keyword를 의미합니다.
}
