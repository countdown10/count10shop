package xyz.tomorrowlearncamp.count10shop.domain.coupon.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.CreateCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.Coupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.CouponStatus;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.repository.CouponRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Test
    @DisplayName("쿠폰_생성_성공")
    void createCoupon_success() {
        //given
        CreateCouponRequest request = CreateCouponRequest.builder()
                .name("쿠폰")
                .content("테스트 쿠폰")
                .discountAmount(3000)
                .minOrderPrice(20000)
                .totalQuantity(10)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        // when
        Long savedId = couponService.createCoupon(request);

        // then
        Coupon saved = couponRepository
                .findById(savedId)
                .orElseThrow(
                        () -> new IllegalArgumentException("저장된 쿠폰이 없습니다."));

        Assertions.assertThat(saved.getName()).isEqualTo("쿠폰");
        Assertions.assertThat(saved.getContent()).isEqualTo("테스트 쿠폰");
        Assertions.assertThat(saved.getDiscountAmount()).isEqualTo(3000);
        Assertions.assertThat(saved.getTotalQuantity()).isEqualTo(10);
        Assertions.assertThat(saved.getIssuedQuantity()).isEqualTo(0);
        Assertions.assertThat(saved.getCouponStatus()).isEqualTo(CouponStatus.CREATED);
        Assertions.assertThat(saved.getExpiredAt()).isAfter(LocalDateTime.now());

    }

    @Test
    void issueCoupon() {
    }

    @Test
    void getCoupons() {
    }

    @Test
    void getUserCoupons() {
    }

    @Test
    void useCoupon() {
    }

    @Test
    void getCoupon() {
    }
}