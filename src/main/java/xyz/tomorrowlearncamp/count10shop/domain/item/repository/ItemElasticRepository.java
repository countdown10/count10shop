package xyz.tomorrowlearncamp.count10shop.domain.item.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import xyz.tomorrowlearncamp.count10shop.domain.item.entity.ItemDocument;

@Repository
public interface ItemElasticRepository extends ElasticsearchRepository<ItemDocument, Long> {
	List<ItemDocument> findByItemName(String name);
}
