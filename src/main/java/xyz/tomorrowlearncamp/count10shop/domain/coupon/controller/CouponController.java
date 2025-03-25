package xyz.tomorrowlearncamp.count10shop.domain.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.CreateCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request.IssueCouponRequest;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.CouponResponse;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response.IssuedCouponResponse;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.Coupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.repository.CouponRepository;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.service.CouponService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CouponController {
    private final CouponService couponService;

    // 쿠폰 생성
    @PostMapping("/coupons")
    public ResponseEntity<Long> createCoupon(@RequestBody CreateCouponRequest createCouponRequest) {
        // TODO: ADMIN 권한 체크 필요
        Long id = couponService.createCoupon(createCouponRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    // 전체 쿠폰 조회
    @GetMapping("/coupons")
    public ResponseEntity<List<CouponResponse>> getCoupons() {
        List<CouponResponse> coupons = couponService.getCoupons();
        return ResponseEntity.ok(coupons);
    }

    // 쿠폰 발급
    @PostMapping("/coupons/{couponId}/issue")
    public ResponseEntity<Void> issueCoupon(@PathVariable Long couponId,
                                            @RequestBody IssueCouponRequest issueCouponRequest) {
        // TODO: USER 권한 체크 필요
        couponService.issueCoupon(couponId, issueCouponRequest.getUserId());
        return ResponseEntity.ok().build();
    }

    // 유저가 발급받은 쿠폰 조회
    @GetMapping("/users/{userId}/coupons")
    public ResponseEntity<List<IssuedCouponResponse>> getUserCoupons(@PathVariable Long userId) {
        List<IssuedCouponResponse> coupons = couponService.getUserCoupons(userId);
        return ResponseEntity.ok(coupons);
    }

    // 사용한 쿠폰 처리 (결제 완료 했을때)
    @PatchMapping("/users/{userId}/coupons/{issuedCouponId}/use")
    public ResponseEntity<Void> useCoupon(@PathVariable Long userId,
                                          @PathVariable Long issuedCouponId) {
        couponService.useCoupon(issuedCouponId, userId);
        return ResponseEntity.ok().build();
    }

    // 단일 쿠폰 조회
    @GetMapping("/coupons/{couponId}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long couponId) {
        CouponResponse response = couponService.getCoupon(couponId);
        return ResponseEntity.ok(response);
    }
}
