package xyz.tomorrowlearncamp.count10shop.domain.coupon.repository;


import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.Coupon;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    // 3. 동시 요청해도 발급 수량 안정적으로 증가
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Coupon c WHERE c.id = :id")
    Optional<Coupon> findByIdForUpdate(@Param("id") Long id);

    // 2. 발급 수량 초과 방지
    @Modifying
    @Query("UPDATE Coupon c SET c.issuedQuantity = c.issuedQuantity+1 WHERE c.id = :id AND c.issuedQuantity < c.totalQuantity")
    int addIssuedQuantitySafe(@Param("id") Long id);

    /*
    마지막은 선착순 쿠폰 발급인데, 사실 이것에 대해 서는 2+3을 하면 되지 않나라는 생각에 일단
    테스트 진행해보고, 만약 안된다면, 그때 되서 추가하는 것으로 진행.
     */
}
