package xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.payment.entity.Payment;

@Getter
@RequiredArgsConstructor
public class PaymentListResponseDto {
	private final Long paymentId;
	private final String itemName;
	private final Long totalPrice;

	public static PaymentListResponseDto of(Payment payment) {
		return new PaymentListResponseDto(payment.getId(), payment.getItemName(), payment.getTotalPrice());
	}
}
