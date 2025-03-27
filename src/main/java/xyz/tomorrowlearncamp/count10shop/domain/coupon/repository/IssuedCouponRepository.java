package xyz.tomorrowlearncamp.count10shop.domain.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.IssuedCoupon;

import java.util.List;
import java.util.Optional;

@Repository
public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    // 1. 중복 발급 방지
    boolean existsByUserIdAndCouponId(Long userId, Long couponId);

    // 발급된 쿠폰 목록 -> TODO: 유저가 10개 쿠폰 발급 받으면, 쿠폰 쿼리 10번 나감 1+N 가능성있음(user 개발되면 할 생각). 개선 필요할 것 같음 (조인 팻치로 메서드 하나 더 추가할생각)
    List<IssuedCoupon> findAllByUserId(Long userId);

}

