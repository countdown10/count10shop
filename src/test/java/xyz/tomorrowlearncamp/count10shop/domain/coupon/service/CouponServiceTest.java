package xyz.tomorrowlearncamp.count10shop.domain.coupon.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.CreateCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.CouponResponse;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.IssuedCouponResponse;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private IssuedCouponRepository issuedCouponRepository;

    @Test
    @DisplayName("쿠폰_생성_성공")
    void createCoupon_success() {
        // given
        CreateCouponRequest request = CreateCouponRequest.builder()
                .name("쿠폰")
                .content("테스트 쿠폰")
                .discountAmount(3000)
                .minOrderPrice(20000)
                .totalQuantity(10)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        Coupon mockCoupon = Coupon.builder()
                .name(request.getName())
                .content(request.getContent())
                .discountAmount(request.getDiscountAmount())
                .minOrderPrice(request.getMinOrderPrice())
                .totalQuantity(request.getTotalQuantity())
                .issuedQuantity(0)
                .couponStatus(CouponStatus.CREATED)
                .expiredAt(request.getExpiredAt())
                .build();

        ReflectionTestUtils.setField(mockCoupon, "id", 1L);

        when(couponRepository.save(any(Coupon.class))).thenReturn(mockCoupon);

        // when
        Long result = couponService.createCoupon(request);

        // then
        assertThat(result).isEqualTo(1L);
        verify(couponRepository, times(1)).save(any(Coupon.class));
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
                .hasMessage("이미 발급된 쿠폰입니다");
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
                .name("수량 초과")
                .content("fail")
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
                .hasMessage("쿠폰이 이미 모두 발급되어 더 이상 남은 수량이 없음.");
    }

    @Test
    @DisplayName("쿠폰_발급_실패-쿠폰_상태_불가능")
    void issueCoupon_fail_4() {
        // given
        Coupon coupon = couponRepository.save(Coupon.builder()
                .name("비활성")
                .content("fail")
                .minOrderPrice(10000)
                .discountAmount(1000)
                .totalQuantity(10)
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
                .hasMessage("'AVAILABLE' 가 아니라서 쿠폰 발급 불가능합니다.");
    }

    @Test
    @DisplayName("전체_쿠폰_목록_조회_성공")
    void getCoupons_success() {
        //given
        couponRepository.save(Coupon.builder()
                .name("첫 번째 쿠폰")
                .content("테스트1")
                .minOrderPrice(10000)
                .discountAmount(1000)
                .totalQuantity(10)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.CREATED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build());

        couponRepository.save(Coupon.builder()
                .name("두 번째 쿠폰")
                .content("테스트2")
                .minOrderPrice(20000)
                .discountAmount(2000)
                .totalQuantity(5)
                .issuedQuantity(1)
                .couponStatus(CouponStatus.CREATED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build());

        // when
        List<CouponResponse> responses = couponService.getCoupons();

        // then
        Assertions.assertThat(responses).hasSize(2);
        Assertions.assertThat(responses.get(0).getName()).isEqualTo("첫 번째 쿠폰");
        Assertions.assertThat(responses.get(1).getName()).isEqualTo("두 번째 쿠폰");

    }

    @Test
    @DisplayName("유저_쿠폰목록_조회_성공")
    void getUserCoupons_success() {
        // given
        CreateCouponRequest request = CreateCouponRequest.builder()
                .name("테스트 쿠폰")
                .content("유저 쿠폰 조회용")
                .discountAmount(2000)
                .minOrderPrice(10000)
                .totalQuantity(5)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        Long couponId = couponService.createCoupon(request);
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 없음"));
        coupon.activate();

        Long userId = 100L;
        couponService.issueCoupon(couponId, userId);

        // when
        List<IssuedCouponResponse> userCoupons = couponService.getUserCoupons(userId);

        // then
        Assertions.assertThat(userCoupons).hasSize(1);
        IssuedCouponResponse response = userCoupons.get(0);
        Assertions.assertThat(response.getUserId()).isEqualTo(userId);
        Assertions.assertThat(response.getCouponId()).isEqualTo(couponId);
        Assertions.assertThat(response.getStatus()).isEqualTo(IssuedCouponStatus.UNUSED);
        Assertions.assertThat(response.getName()).isEqualTo("테스트 쿠폰");
        Assertions.assertThat(response.getContent()).isEqualTo("유저 쿠폰 조회용");
        Assertions.assertThat(response.getDiscountAmount()).isEqualTo(2000);
    }

    @Test
    @DisplayName("유저_쿠폰목록_조회_실패_없을때")
    void getUserCoupons_fail() {
        //given
        Long userId = 9L;

        // when
        List<IssuedCouponResponse> userCoupons = couponService.getUserCoupons(userId);

        // then
        Assertions.assertThat(userCoupons).isEmpty();
    }

    @Test
    @DisplayName("쿠폰_사용_성공")
    void useCoupon_success() {
        //given
        Coupon coupon = createCoupon();
        Long userId = 1L;
        IssuedCoupon issuedCoupon = issuedCouponRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .coupon(coupon)
                        .status(IssuedCouponStatus.UNUSED)
                        .issuedAt(LocalDateTime.now())
                        .build()
        );

        // when
        couponService.useCoupon(issuedCoupon.getId(), userId);

        // then
        IssuedCoupon updated = issuedCouponRepository.findById(issuedCoupon.getId()).get();
        Assertions.assertThat(updated.getStatus()).isEqualTo(IssuedCouponStatus.USED);
        Assertions.assertThat(updated.getUsedAt()).isNotNull();
    }

    @Test
    @DisplayName("쿠폰_사용_실패-없는_쿠폰")
    void useCoupon_fail_1() {
        //given
        Long noneId = 9L;
        Long userId = 1L;

        // when
        Throwable throwable = catchThrowable(() -> couponService.useCoupon(noneId, userId));

        // then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급된 쿠폰이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("쿠폰_사용_실패-타인꺼")
    void useCoupon_fail_2() {
        //given
        Coupon coupon = createCoupon();
        Long ownerId = 1L;
        Long mysteriousId = 2L;

        IssuedCoupon issuedCoupon = issuedCouponRepository.save(
                IssuedCoupon.builder()
                        .userId(ownerId)
                        .coupon(coupon)
                        .status(IssuedCouponStatus.UNUSED)
                        .issuedAt(LocalDateTime.now())
                        .build()
        );

        // when
        Throwable throwable = catchThrowable(() -> couponService.useCoupon(issuedCoupon.getId(), mysteriousId));

        // then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 쿠폰은 고객님의 것이 아닙니다.");
    }

    @Test
    @DisplayName("쿠폰_사용_실패-이미_사용된_쿠폰")
    void useCoupon_fail_3() {
        //given
        Coupon coupon = createCoupon();
        Long userId = 1L;

        IssuedCoupon issuedCoupon = issuedCouponRepository.save(
                IssuedCoupon.builder()
                        .userId(userId)
                        .coupon(coupon)
                        .status(IssuedCouponStatus.USED)
                        .issuedAt(LocalDateTime.now())
                        .usedAt(LocalDateTime.now())
                        .build()
        );

        // when
        Throwable throwable = catchThrowable(() -> couponService.useCoupon(issuedCoupon.getId(), userId));

        // then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");
    }

    @Test
    @DisplayName("쿠폰_단일_조회_성공")
    void getCoupon_success() {
        //given
        CreateCouponRequest request = CreateCouponRequest.builder()
                .name("쿠폰")
                .content("단일 조회 테스트")
                .minOrderPrice(10000)
                .discountAmount(2000)
                .totalQuantity(10)
                .expiredAt(LocalDateTime.now().plusDays(3))
                .build();

        Long couponId = couponService.createCoupon(request);

        // when
        CouponResponse response = couponService.getCoupon(couponId);

        // then
        Assertions.assertThat(response.getId()).isEqualTo(couponId);
        Assertions.assertThat(response.getName()).isEqualTo("쿠폰");
        Assertions.assertThat(response.getContent()).isEqualTo("단일 조회 테스트");
        Assertions.assertThat(response.getDiscountAmount()).isEqualTo(2000);
        Assertions.assertThat(response.getTotalQuantity()).isEqualTo(10);
        Assertions.assertThat(response.getMinOrderPrice()).isEqualTo(10000);
    }

    @Test
    @DisplayName("쿠폰_단일_조회_실패")
    void getCoupon_fail() {
        //given
        Long noneId = 1L;

        // when
        Throwable throwable = Assertions.catchThrowable(() -> couponService.getCoupon(noneId));

        // then
        Assertions.assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 쿠폰입니다.");
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
}