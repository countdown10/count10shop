package xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.IssuedCoupon;
import xyz.tomorrowlearncamp.count10shop.domain.coupon.entity.IssuedCouponStatus;

/**
 * @author 문성준
 * @Content 발급된 쿠폰
 * @Date 25.03.25 19:00
 */

@Getter
@NoArgsConstructor
public class IssuedCouponResponse {
	private Long issuedCouponId;
	private Long couponId;
	private Long userId;
	private String name;
	private String content;
	private Integer discountAmount;
	private Integer minOrderPrice;
	private IssuedCouponStatus status;
	private LocalDateTime issuedAt;
	private LocalDateTime usedAt;
	private LocalDateTime expiredAt;

	public static IssuedCouponResponse of(IssuedCoupon issuedCoupon) {
		IssuedCouponResponse response = new IssuedCouponResponse();
		response.issuedCouponId = issuedCoupon.getId();
		response.couponId = issuedCoupon.getCoupon().getId();
		response.userId = issuedCoupon.getUserId();
		response.name = issuedCoupon.getCoupon().getName();
		response.content = issuedCoupon.getCoupon().getContent();
		response.discountAmount = issuedCoupon.getCoupon().getDiscountAmount();
		response.minOrderPrice = issuedCoupon.getCoupon().getMinOrderPrice();
		response.expiredAt = issuedCoupon.getCoupon().getExpiredAt();
		response.status = issuedCoupon.getStatus();
		response.issuedAt = issuedCoupon.getIssuedAt();
		response.usedAt = issuedCoupon.getUsedAt();
		return response;
	}
}
