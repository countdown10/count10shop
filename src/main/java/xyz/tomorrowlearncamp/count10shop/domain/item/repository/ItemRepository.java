package xyz.tomorrowlearncamp.count10shop.domain.item.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	@Query("SELECT i FROM Item i "
		+ "WHERE (i.category = :category OR :category IS NULL)")
	Page<Item> findByCategory(@Param("category") Category category, Pageable pageable);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select i from Item i where i.id = :id")
	Optional<Item> findByIdWithPessimisticLock(Long id);

	default Item findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new InvalidRequestException("상품을 찾을 수 없습니다."));
	}

	default Item findByIdWithPessimisticLockOrEseThrow(Long id) {
		return findByIdWithPessimisticLock(id).orElseThrow(() -> new InvalidRequestException("상품을 찾을 수 없습니다."));
	}
}
