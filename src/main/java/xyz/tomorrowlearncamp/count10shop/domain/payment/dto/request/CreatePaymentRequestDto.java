package xyz.tomorrowlearncamp.count10shop.domain.payment.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreatePaymentRequestDto {
	@Min(1)
	private final Long userId;

	@NotBlank
	private final String password;

	@Min(1)
	private final Long itemId;

	private final Long issuedCouponId;
}
