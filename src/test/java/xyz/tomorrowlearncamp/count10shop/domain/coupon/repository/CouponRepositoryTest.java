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

@DataJpaTest
@ActiveProfiles("dev")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CouponRepositoryTest {
    @Autowired
    private CouponRepository couponRepository;


    @Test
    @DisplayName("쿠폰_리포지토리_쿠폰_발급_수량_증가(실패)")
    void addIssuedQuantitySafe_fail() {
        //given
        Coupon coupon = Coupon.builder()
                .name("테스트")
                .content("테스트 쿠폰")
                .minOrderPrice(5000)
                .discountAmount(1000)
                .totalQuantity(5)
                .issuedQuantity(4)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        couponRepository.save(coupon);

        //when
        int updateCount = couponRepository.addIssuedQuantitySafe(coupon.getId());

        //then
        Assertions.assertThat(updateCount).isEqualTo(1);
    }

    @Test
    @DisplayName("쿠폰_리포지토리_쿠폰_발급_수량_증가(성공)")
    void addIssuedQuantitySafe_success() {
        //given
        Coupon coupon = Coupon.builder()
                .name("테스트")
                .content("테스트 쿠폰")
                .minOrderPrice(5000)
                .discountAmount(1000)
                .totalQuantity(5)
                .issuedQuantity(2)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        couponRepository.save(coupon);

        //when
        int updateCount = couponRepository.addIssuedQuantitySafe(coupon.getId());

        //then
        Assertions.assertThat(updateCount).isEqualTo(1);
    }

    @Test
    @DisplayName("쿠폰_리포지토리_id_조회_실패")
    void findByIdForUpdate_fail() {
        // given
        Long notExistsId = 999L;

        // when, then
        Assertions.assertThatThrownBy(() -> {
                    couponRepository.findByIdForUpdate(notExistsId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
                }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 쿠폰입니다.");
    }

    @Test
    @DisplayName("쿠폰_리포지토리_id_조회_성공")
    void findByIdForUpdate_success() {
        // given
        Coupon coupon = Coupon.builder()
                .name("락 테스트")
                .content("락 테스트용")
                .minOrderPrice(5000)
                .discountAmount(1000)
                .totalQuantity(10)
                .issuedQuantity(1)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();
        Coupon saved = couponRepository.save(coupon);

        // when
        Coupon found = couponRepository.findByIdForUpdate(saved.getId())
                .orElseThrow(() -> new RuntimeException("쿠폰이 존재하지 않습니다"));

        // then
        Assertions.assertThat(found.getId()).isEqualTo(saved.getId());
    }
}