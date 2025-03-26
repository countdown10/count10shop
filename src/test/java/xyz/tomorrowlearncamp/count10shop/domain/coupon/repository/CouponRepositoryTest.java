package xyz.tomorrowlearncamp.count10shop.domain.coupon.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.Coupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.CouponStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CouponRepositoryTest {
    @Autowired
    private CouponRepository couponRepository;

    @Test
    void findByIdForUpdate() {

    }

    @Test
    @DisplayName("동시성 테스트 - 발급 수량 증가")
    void addIssuedQuantitySafe() {
        //given
        Coupon coupon = Coupon.builder()
                .name("lock 테스트 해볼거")
                .content("lock 테스트 쿠폰")
                .minOrderPrice(5000)
                .discountAmount(1000)
                .totalQuantity(5)
                .issuedQuantity(4)
                .couponStatus(CouponStatus.ACTIVE)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        couponRepository.save(coupon);

        //when
        int updateCount = couponRepository.addIssuedQuantitySafe(coupon.getId());

        //then
        Assertions.assertThat(updateCount).isEqualTo(0);
    }

}