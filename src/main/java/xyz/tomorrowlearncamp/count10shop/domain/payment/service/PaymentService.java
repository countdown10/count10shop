package xyz.tomorrowlearncamp.count10shop.domain.payment.service;

import org.springframework.data.domain.Page;

import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentResponseDto;

public interface PaymentService {
	Page<PaymentListResponseDto> findAll(int page, int size);
	PaymentResponseDto findById(Long id);
	PaymentResponseDto purchaseItem(Long itemId);
}
