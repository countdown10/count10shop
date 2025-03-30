package xyz.tomorrowlearncamp.count10shop.domain.coupon.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

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

        when(couponRepository.save(any(Coupon.class)))
                .thenThrow(new NullPointerException("name"));

        // when
        Throwable throwable = catchThrowable(() -> couponService.createCoupon(createCouponRequest));

        // then
        assertThat(throwable)
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name");
        /*
        name은 필수값인데, null로 주니까 하이버네이트가 insert쿼리 날리다가 db에러[23502-232]받음.
         */
    }

    @Test
    @DisplayName("쿠폰_발급_성공")
    void issueCoupon_success() {
        // given
        Long couponId = 1L;
        Long userId = 1L;

        Coupon coupon = Coupon.builder()
                .name("할인 쿠폰")
                .content("테스트용")
                .minOrderPrice(5000)
                .discountAmount(1000)
                .totalQuantity(10)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();
        ReflectionTestUtils.setField(coupon, "id", couponId);

        when(issuedCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(false);
        when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.of(coupon));
        when(issuedCouponRepository.save(any(IssuedCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        couponService.issueCoupon(couponId, userId);

        // then
        assertThat(coupon.getIssuedQuantity()).isEqualTo(1);
        verify(issuedCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verify(couponRepository).findByIdForUpdate(couponId);
        verify(issuedCouponRepository).save(any(IssuedCoupon.class));
    }


    @Test
    @DisplayName("쿠폰_발급_실패-이미_발급된_쿠폰")
    void issueCoupon_fail_1() {
        // given
        Long couponId = 1L;
        Long userId = 1L;

        when(issuedCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(true);

        // when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(couponId, userId));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 발급된 쿠폰입니다");

        verify(issuedCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verifyNoMoreInteractions(couponRepository, issuedCouponRepository);
    }

    @Test
    @DisplayName("쿠폰_발급_실패-없는_쿠폰")
    void issueCoupon_fail_2() {
        //given
        Long couponId = 999L;
        Long userId = 1L;

        when(issuedCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(false);
        when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(couponId, userId));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("없는 쿠폰입니다.");

        verify(issuedCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verify(couponRepository).findByIdForUpdate(couponId);
    }

    @Test
    @DisplayName("쿠폰_발급_실패-발급_수량_초과")
    void issueCoupon_fail_3() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        Coupon coupon = Coupon.builder()
                .name("수량 초과 쿠폰")
                .content("수량 초과 테스트")
                .minOrderPrice(10000)
                .discountAmount(2000)
                .totalQuantity(1)
                .issuedQuantity(1) // 이미 다 발급됨
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        // mock 설정
        when(issuedCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(false);
        when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.of(coupon));

        // when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(couponId, userId));

        // then
        assertThat(throwable).isInstanceOf(IllegalStateException.class)
                .hasMessage("쿠폰이 이미 모두 발급되어 더 이상 남은 수량이 없음.");

        verify(issuedCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verify(couponRepository).findByIdForUpdate(couponId);
        verifyNoMoreInteractions(issuedCouponRepository);
    }

    @Test
    @DisplayName("쿠폰_발급_실패-쿠폰_상태_불가능")
    void issueCoupon_fail_4() {
        // given
        Long userId = 1L;
        Long couponId = 1L;

        Coupon coupon = Coupon.builder()
                .name("비활성 쿠폰")
                .content("쿠폰 상태 테스트(fail)")
                .minOrderPrice(10000)
                .discountAmount(2000)
                .totalQuantity(10)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.CREATED)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        // mock 설정
        when(issuedCouponRepository.existsByUserIdAndCouponId(userId, couponId)).thenReturn(false);
        when(couponRepository.findByIdForUpdate(couponId)).thenReturn(Optional.of(coupon));

        // when
        Throwable throwable = catchThrowable(() -> couponService.issueCoupon(couponId, userId));

        // then
        assertThat(throwable).isInstanceOf(IllegalStateException.class)
                .hasMessage("'AVAILABLE' 가 아니라서 쿠폰 발급 불가능합니다.");

        verify(issuedCouponRepository).existsByUserIdAndCouponId(userId, couponId);
        verify(couponRepository).findByIdForUpdate(couponId);
        verifyNoMoreInteractions(issuedCouponRepository);
    }

    @Test
    @DisplayName("전체_쿠폰_목록_조회_성공")
    void getCoupons_success() {
        // given
        Coupon coupon1 = Coupon.builder()
                .name("첫 번째 쿠폰")
                .content("테스트1")
                .minOrderPrice(10000)
                .discountAmount(1000)
                .totalQuantity(10)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.CREATED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        Coupon coupon2 = Coupon.builder()
                .name("두 번째 쿠폰")
                .content("테스트2")
                .minOrderPrice(20000)
                .discountAmount(2000)
                .totalQuantity(5)
                .issuedQuantity(1)
                .couponStatus(CouponStatus.CREATED)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        List<Coupon> couponList = List.of(coupon1, coupon2);
        Mockito.when(couponRepository.findAll()).thenReturn(couponList);

        // when
        List<CouponResponse> responses = couponService.getCoupons();

        // then
        Assertions.assertThat(responses).hasSize(2);
        Assertions.assertThat(responses.get(0).getName()).isEqualTo("첫 번째 쿠폰");
        Assertions.assertThat(responses.get(1).getName()).isEqualTo("두 번째 쿠폰");

        Mockito.verify(couponRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("유저_쿠폰목록_조회_성공")
    void getUserCoupons_success() {
        // given
        Long userId = 100L;
        Coupon coupon = Coupon.builder()
                .name("테스트 쿠폰")
                .content("유저 쿠폰 조회용")
                .minOrderPrice(10000)
                .discountAmount(2000)
                .totalQuantity(5)
                .issuedQuantity(1)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .status(IssuedCouponStatus.UNUSED)
                .issuedAt(LocalDateTime.now())
                .build();

        Mockito.when(issuedCouponRepository.findAllByUserId(userId))
                .thenReturn(List.of(issuedCoupon));

        // when
        List<IssuedCouponResponse> userCoupons = couponService.getUserCoupons(userId);

        // then
        Assertions.assertThat(userCoupons).hasSize(1);
        IssuedCouponResponse response = userCoupons.get(0);
        Assertions.assertThat(response.getUserId()).isEqualTo(userId);
        Assertions.assertThat(response.getCouponId()).isEqualTo(issuedCoupon.getCoupon().getId());
        Assertions.assertThat(response.getStatus()).isEqualTo(IssuedCouponStatus.UNUSED);
        Assertions.assertThat(response.getName()).isEqualTo("테스트 쿠폰");
        Assertions.assertThat(response.getContent()).isEqualTo("유저 쿠폰 조회용");
        Assertions.assertThat(response.getDiscountAmount()).isEqualTo(2000);

        Mockito.verify(issuedCouponRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("유저_쿠폰목록_조회_실패_없을때")
    void getUserCoupons_fail() {
        // given
        Long userId = 9L;
        Mockito.when(issuedCouponRepository.findAllByUserId(userId))
                .thenReturn(List.of());

        // when
        List<IssuedCouponResponse> userCoupons = couponService.getUserCoupons(userId);

        // then
        Assertions.assertThat(userCoupons).isEmpty();
        Mockito.verify(issuedCouponRepository, times(1)).findAllByUserId(userId);
    }

    @Test
    @DisplayName("쿠폰_사용_성공")
    void useCoupon_success() {
        // given
        Long userId = 1L;
        Long issuedCouponId = 10L;
        Coupon coupon = Coupon.builder()
                .name("쿠폰")
                .content("테스트")
                .minOrderPrice(10000)
                .discountAmount(1000)
                .totalQuantity(10)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();
        ReflectionTestUtils.setField(coupon, "id", 1L);

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .status(IssuedCouponStatus.UNUSED)
                .issuedAt(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(issuedCoupon, "id", issuedCouponId);

        when(issuedCouponRepository.findById(issuedCouponId)).thenReturn(Optional.of(issuedCoupon));

        // when
        couponService.useCoupon(issuedCouponId, userId);

        // then
        assertThat(issuedCoupon.getStatus()).isEqualTo(IssuedCouponStatus.USED);
        assertThat(issuedCoupon.getUsedAt()).isNotNull();

        verify(issuedCouponRepository, times(1)).findById(issuedCouponId);
    }

    @Test
    @DisplayName("쿠폰_사용_실패-없는_쿠폰")
    void useCoupon_fail_1() {
        // given
        Long issuedCouponId = 9L;
        Long userId = 1L;

        when(issuedCouponRepository.findById(issuedCouponId)).thenReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() ->
                couponService.useCoupon(issuedCouponId, userId));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("발급된 쿠폰이 존재하지 않습니다.");

        verify(issuedCouponRepository, times(1)).findById(issuedCouponId);
    }

    @Test
    @DisplayName("쿠폰_사용_실패-타인꺼")
    void useCoupon_fail_2() {
        // given
        Long userId = 1L;
        Long otherUserId = 2L;
        Long issuedCouponId = 10L;

        Coupon coupon = Coupon.builder()
                .name("쿠폰")
                .content("타인 쿠폰")
                .minOrderPrice(10000)
                .discountAmount(2000)
                .totalQuantity(10)
                .issuedQuantity(1)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .id(issuedCouponId)
                .userId(otherUserId)
                .coupon(coupon)
                .status(IssuedCouponStatus.UNUSED)
                .issuedAt(LocalDateTime.now())
                .build();

        when(issuedCouponRepository.findById(issuedCouponId)).thenReturn(Optional.of(issuedCoupon));

        // when
        Throwable throwable = catchThrowable(() -> couponService.useCoupon(issuedCouponId, userId));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 쿠폰은 고객님의 것이 아닙니다.");

        verify(issuedCouponRepository, times(1)).findById(issuedCouponId);
    }

    @Test
    @DisplayName("쿠폰_사용_실패-이미_사용된_쿠폰")
    void useCoupon_fail_3() {
        // given
        Long userId = 1L;
        Long issuedCouponId = 10L;

        Coupon coupon = Coupon.builder()
                .name("쿠폰")
                .content("사용된 쿠폰")
                .minOrderPrice(10000)
                .discountAmount(2000)
                .totalQuantity(10)
                .issuedQuantity(1)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(5))
                .build();

        IssuedCoupon issuedCoupon = IssuedCoupon.builder()
                .id(issuedCouponId)
                .userId(userId)
                .coupon(coupon)
                .status(IssuedCouponStatus.USED)
                .issuedAt(LocalDateTime.now())
                .usedAt(LocalDateTime.now())
                .build();

        when(issuedCouponRepository.findById(issuedCouponId)).thenReturn(Optional.of(issuedCoupon));

        // when
        Throwable throwable = catchThrowable(() -> couponService.useCoupon(issuedCouponId, userId));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 사용된 쿠폰입니다.");

        verify(issuedCouponRepository, times(1)).findById(issuedCouponId);
    }

    @Test
    @DisplayName("쿠폰_단일_조회_성공")
    void getCoupon_success() {
        // given
        Long couponId = 1L;

        Coupon coupon = Coupon.builder()
                .name("쿠폰")
                .content("단일 조회 테스트")
                .minOrderPrice(10000)
                .discountAmount(2000)
                .totalQuantity(10)
                .issuedQuantity(0)
                .couponStatus(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusDays(3))
                .build();

        ReflectionTestUtils.setField(coupon, "id", couponId);

        when(couponRepository.findById(couponId)).thenReturn(Optional.of(coupon));

        // when
        CouponResponse response = couponService.getCoupon(couponId);

        // then
        assertThat(response.getId()).isEqualTo(couponId);
        assertThat(response.getName()).isEqualTo("쿠폰");
        assertThat(response.getContent()).isEqualTo("단일 조회 테스트");
        assertThat(response.getDiscountAmount()).isEqualTo(2000);
        assertThat(response.getTotalQuantity()).isEqualTo(10);
        assertThat(response.getMinOrderPrice()).isEqualTo(10000);

        verify(couponRepository, times(1)).findById(couponId);
    }

    @Test
    @DisplayName("쿠폰_단일_조회_실패")
    void getCoupon_fail() {
        //given
        Long couponId = 99L;

        when(couponRepository.findById(couponId)).thenReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> couponService.getCoupon(couponId));

        // then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 쿠폰입니다.");

        verify(couponRepository, times(1)).findById(couponId);
    }
}