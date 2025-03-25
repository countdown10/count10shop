package xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.Coupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.CouponStatus;

import java.time.LocalDateTime;

/**
 * @author 문성준
 * @Content 쿠폰 정보
 * @Date 25.03.25 18:42
 */
@Getter
@NoArgsConstructor
public class CouponResponse {
    private Long id;
    private String name;
    private String content;
    private Integer discountAmount;
    private Integer minOrderPrice;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private CouponStatus status;
    private LocalDateTime expiredAt;


    public static CouponResponse from(Coupon coupon) {
        CouponResponse couponResponse = new CouponResponse();
        couponResponse.id = coupon.getId();
        couponResponse.name = coupon.getName();
        couponResponse.content = coupon.getContent();
        couponResponse.discountAmount = coupon.getDiscountAmount();
        couponResponse.minOrderPrice = coupon.getMinOrderPrice();
        couponResponse.totalQuantity = coupon.getTotalQuantity();
        couponResponse.issuedQuantity = coupon.getTotalQuantity();
        couponResponse.status = coupon.getCouponStatus();
        couponResponse.expiredAt = coupon.getExpiredAt();
        return couponResponse;
    }
}
