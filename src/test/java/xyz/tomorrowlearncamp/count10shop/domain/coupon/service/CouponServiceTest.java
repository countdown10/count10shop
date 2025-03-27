package xyz.tomorrowlearncamp.count10shop.domain.coupon.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.CreateCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.Coupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.CouponStatus;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.IssuedCoupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.IssuedCouponStatus;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.repository.CouponRepository;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.repository.IssuedCouponRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CouponServiceTest {
    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private IssuedCouponRepository issuedCouponRepository;

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
    @DisplayName("쿠폰_생성_실패-필수값_누락")
    void createCoupon_fail() {
        //given
        CreateCouponRequest createCouponRequest = CreateCouponRequest.builder()
                .name(null)
                .content("쿠폰 생성 실패")
                .minOrderPrice(20000)
                .discountAmount(3000)
                .totalQuantity(10)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();
        // when
        Throwable throwable = catchThrowable(() -> couponService.createCoupon(createCouponRequest));

        // then
        assertThat(throwable).isInstanceOf(Exception.class);
        /*
        name은 필수값인데, null로 주니까 하이버네이트가 insert쿼리 날리다가 db에러[23502-232]받음.
         */
    }

    @Test
    @DisplayName("쿠폰_발급_성공")
    void issueCoupon_success() {
        //given
        CreateCouponRequest createCouponRequest = CreateCouponRequest.builder()
                .name("할인 쿠폰")
                .content("테스트용")
                .minOrderPrice(5000)
                .discountAmount(1000)
                .totalQuantity(10)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        Long couponId = couponService.createCoupon(createCouponRequest);
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 없음"));

        coupon.activate();

        Long userId = 1L;

        // when
        couponService.issueCoupon(couponId, userId);

        // then
        List<IssuedCoupon> issuedCoupons = issuedCouponRepository.findAllByUserId(userId);
        Assertions.assertThat(issuedCoupons).hasSize(1);
        Assertions.assertThat(issuedCoupons.get(0).getCoupon().getId()).isEqualTo(couponId);
        Assertions.assertThat(issuedCoupons.get(0).getUserId()).isEqualTo(userId);
        Assertions.assertThat(issuedCoupons.get(0).getStatus()).isEqualTo(IssuedCouponStatus.UNUSED);
        // 발급 수량 증가 확인
        Coupon updatedCoupon = couponRepository.findById(couponId).get();
        Assertions.assertThat(updatedCoupon.getIssuedQuantity()).isEqualTo(1);
    }


    private Coupon createCoupon() {
        Coupon coupon = Coupon.builder()
                .name("테스트")
                .content("테스트")
                .discountAmount(3000)
                .minOrderPrice(20000)
                .totalQuantity(10)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();
        return couponRepository.save(coupon);
    }
    @Test
    @DisplayName("쿠폰_발급_실패-이미_발급된_쿠폰")
    void issueCoupon_fail_1() {
        //given
        Long userId = 1L;
        Coupon coupon = createCoupon();

        issuedCouponRepository.save(IssuedCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .status(IssuedCouponStatus.UNUSED)
                .issuedAt(LocalDateTime.now())
                .build());

        //when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(coupon.getId(), userId));

        //then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 발급된 쿠폰");
    }

    @Test
    @DisplayName("쿠폰_발급_실패-없는_쿠폰")
    void issueCoupon_fail_2() {
        //given
        Long noneId = 999L;
        Long userId = 1L;

        // when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(noneId, userId));

        // then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 쿠폰입니다.");

    }

    @Test
    @DisplayName("쿠폰_발급_실패-발급_수량_초과")
    void issueCoupon_fail_3() {
        // given
        Coupon coupon = couponRepository.save(Coupon.builder()
                .name("발급 끝!")
                .content("이미 모두 발급해서 더이상 남은 수량이 없음")
                .minOrderPrice(10000)
                .discountAmount(1000)
                .totalQuantity(1)
                .issuedQuantity(1)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build());
        Long userId = 1L;

        // when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(coupon.getId(), userId));

        // then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰을 모두 사용했습니다.");
    }

    @Test
    @DisplayName("쿠폰_발급_실패-쿠폰_상태_불가능")
    void issueCoupon_fail_4() {
        // given
        Coupon coupon = couponRepository.save(Coupon.builder()
                .name("비활성 쿠폰")
                .content("상태 비활성")
                .minOrderPrice(10000)
                .discountAmount(1000)
                .totalQuantity(5)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.CREATED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build());
        Long userId = 1L;

        // when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(coupon.getId(), userId));

        // then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("'AVAILABLE' 가 아니라서 쿠폰 발급 불가능");
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