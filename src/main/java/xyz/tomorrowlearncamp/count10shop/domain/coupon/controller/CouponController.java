package xyz.tomorrowlearncamp.count10shop.domain.coupon.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.CreateCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.IssueCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.CouponResponse;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.IssuedCouponResponse;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.service.CouponService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CouponController {
	private final CouponService couponService;

	// 쿠폰 생성
	@PostMapping("/v1/coupons")
	public ResponseEntity<Long> createCoupon(@RequestBody @Valid CreateCouponRequest createCouponRequest) {
		// TODO: ADMIN 권한 체크 필요
		Long id = couponService.createCoupon(createCouponRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(id);
	}

	// 전체 쿠폰 조회
	@GetMapping("/v1/coupons")
	public ResponseEntity<List<CouponResponse>> getCoupons() {
		List<CouponResponse> coupons = couponService.getCoupons();
		return ResponseEntity.ok(coupons);
	}

	// 쿠폰 발급
	@PostMapping("/v1/coupons/{couponId}/issue")
	public ResponseEntity<Void> issueCoupon(@PathVariable Long couponId,
		@RequestBody @Valid IssueCouponRequest issueCouponRequest) {
		// TODO: USER 권한 체크 필요
		couponService.issueCoupon(couponId, issueCouponRequest.getUserId());
		return ResponseEntity.ok().build();
	}

	// 유저가 발급받은 쿠폰 조회
	@GetMapping("/v1/users/{userId}/coupons")
	public ResponseEntity<List<IssuedCouponResponse>> getUserCoupons(@PathVariable Long userId) {
		List<IssuedCouponResponse> coupons = couponService.getUserCoupons(userId);
		return ResponseEntity.ok(coupons);
	}

	// 사용한 쿠폰 처리 (결제 완료 했을때)
	@Deprecated
	@PatchMapping("/v1/users/{userId}/coupons/{issuedCouponId}/use")
	public ResponseEntity<Void> useCoupon(@PathVariable Long userId,
		@PathVariable Long issuedCouponId) {
		couponService.useCoupon(issuedCouponId, userId);
		return ResponseEntity.ok().build();
	}

	// 단일 쿠폰 조회
	@GetMapping("/v1/coupons/{couponId}")
	public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long couponId) {
		CouponResponse response = couponService.getCoupon(couponId);
		return ResponseEntity.ok(response);
	}
}
