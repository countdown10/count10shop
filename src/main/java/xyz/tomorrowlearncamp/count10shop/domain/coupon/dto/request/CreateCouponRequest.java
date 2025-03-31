package xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

/**
 * @author 문성준
 * @Content 쿠폰 생성
 * @Date 25.03.25 18:42
 */
@Getter
public class CreateCouponRequest {

	private final String name;
	private final String content;
	private final Integer discountAmount;
	private final Integer minOrderPrice;
	private final Integer totalQuantity;
	private final LocalDateTime expiredAt;

	@Builder
	public CreateCouponRequest(String name, String content, Integer discountAmount, Integer minOrderPrice,
		Integer totalQuantity, LocalDateTime expiredAt) {
		this.name = name;
		this.content = content;
		this.discountAmount = discountAmount;
		this.minOrderPrice = minOrderPrice;
		this.totalQuantity = totalQuantity;
		this.expiredAt = expiredAt;
	}
}
