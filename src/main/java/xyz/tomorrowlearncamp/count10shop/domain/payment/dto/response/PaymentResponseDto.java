package xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.payment.entity.Payment;

@Getter
@RequiredArgsConstructor
public class PaymentResponseDto {
	private final Long paymentId;
	private final Long itemId;
	private final String itemName;
	private final Long totalPrice;

	public static PaymentResponseDto of(Payment payment) {
		return new PaymentResponseDto(payment.getId(), payment.getItemId(), payment.getItemName(),
			payment.getTotalPrice());
	}
}
