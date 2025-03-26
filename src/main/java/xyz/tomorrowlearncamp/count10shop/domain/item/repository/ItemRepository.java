package xyz.tomorrowlearncamp.count10shop.domain.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	@Query("SELECT i FROM Item i "
		+ "WHERE (i.category = :category OR :category IS NULL)")
	Page<Item> findByCategory(@Param("category") Category category, Pageable pageable);

	default Item findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new InvalidRequestException("상품을 찾을 수 없습니다."));
	}
}
