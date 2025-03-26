package xyz.tomorrowlearncamp.count10shop.domain.payment.service;

import org.springframework.data.domain.Page;

import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentResponseDto;

public class OptimisticLockPaymentService implements PaymentService {
	@Override
	public Page<PaymentListResponseDto> findAll(int page, int size) {
		return null;
	}

	@Override
	public PaymentResponseDto findById(Long id) {
		return null;
	}

	@Override
	public PaymentResponseDto purchaseItem(Long itemId) {
		return null;
	}
}
