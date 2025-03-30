package xyz.tomorrowlearncamp.count10shop.domain.coupon.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.CreateCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.CouponResponse;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.IssuedCouponResponse;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.Coupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.CouponStatus;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.IssuedCoupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.IssuedCouponStatus;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.repository.CouponRepository;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.repository.IssuedCouponRepository;

@Service
@RequiredArgsConstructor
public class CouponService {
	private final CouponRepository couponRepository;
	private final IssuedCouponRepository issuedCouponRepository;

	// 쿠폰 생성 - TODO: ADMIN 만 가능하게 수정해야함.
	public Long createCoupon(CreateCouponRequest request) {
		Coupon coupon = Coupon.builder()
			.name(request.getName())
			.content(request.getContent())
			.minOrderPrice(request.getMinOrderPrice())
			.discountAmount(request.getDiscountAmount())
			.totalQuantity(request.getTotalQuantity())
			.issuedQuantity(0)
			.couponStatus(CouponStatus.CREATED)
			.expiredAt(request.getExpiredAt())
			.build();
		return couponRepository.save(coupon).getId();
	}

	// 쿠폰 발급 (동시성 제어 포함)
	@Transactional
	public void issueCoupon(Long couponId, Long userId) {
		if (issuedCouponRepository.existsByUserIdAndCouponId(userId, couponId)) {
			throw new IllegalStateException("이미 발급된 쿠폰입니다");
		}

		Coupon coupon = couponRepository.findByIdForUpdate(couponId)
			.orElseThrow(() -> new IllegalArgumentException("없는 쿠폰입니다."));

		if (coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
			throw new IllegalStateException("쿠폰이 이미 모두 발급되어 더 이상 남은 수량이 없음.");
		}
		coupon.addIssuedQuantity();

		if (coupon.getCouponStatus() != CouponStatus.AVAILABLE) {
			throw new IllegalStateException("'AVAILABLE' 가 아니라서 쿠폰 발급 불가능합니다.");
		}

		IssuedCoupon issuedCoupon = IssuedCoupon.builder()
			.userId(userId)
			.coupon(coupon)
			.status(IssuedCouponStatus.UNUSED)
			.issuedAt(LocalDateTime.now())
			.build();
		issuedCouponRepository.save(issuedCoupon);
	}

	// 전체 쿠폰 목록
	public List<CouponResponse> getCoupons() {
		return couponRepository.findAll()
			.stream()
			.map(CouponResponse::of)
			.collect(Collectors.toList());
	}

	// 유저가 발급받은 쿠폰 목록
	public List<IssuedCouponResponse> getUserCoupons(Long userId) {
		return issuedCouponRepository.findAllByUserId(userId)
			.stream()
			.map(IssuedCouponResponse::of)
			.collect(Collectors.toList());
	}

	// 쿠폰 사용 처리
	@Transactional
	public void useCoupon(Long issuedCouponId, Long userId) {
		IssuedCoupon issuedCoupon = issuedCouponRepository.findById(issuedCouponId)
			.orElseThrow(() -> new IllegalArgumentException("발급된 쿠폰이 존재하지 않습니다."));

		if (!issuedCoupon.getUserId().equals(userId)) {
			throw new IllegalStateException("해당 쿠폰은 고객님의 것이 아닙니다.");
		}

		if (issuedCoupon.getStatus() == IssuedCouponStatus.USED) {
			throw new IllegalStateException("이미 사용된 쿠폰입니다.");
		}

		issuedCoupon.use();
	}

	// 단일 쿠폰
	public CouponResponse getCoupon(Long couponId) {
		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(
				() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
		return CouponResponse.of(coupon);
	}
}
