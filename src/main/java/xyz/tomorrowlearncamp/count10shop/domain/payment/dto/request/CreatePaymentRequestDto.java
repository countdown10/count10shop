package xyz.tomorrowlearncamp.count10shop.domain.payment.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreatePaymentRequestDto {
	@Min(1)
	private final Long itemId;

	// todo: coupon 도메인 완료시 추가
	private final Long issuedCouponId;
}
