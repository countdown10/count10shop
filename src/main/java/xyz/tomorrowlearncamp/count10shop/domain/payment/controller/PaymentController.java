package xyz.tomorrowlearncamp.count10shop.domain.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.request.CreatePaymentRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

	private final PaymentService paymentService;

	@GetMapping("/v1/payments")
	public ResponseEntity<Page<PaymentListResponseDto>> findAll(
		// todo: page 객체로
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "10") int size
	) {
		return ResponseEntity.ok(paymentService.findAll(page, size));
	}

	@GetMapping("/v1/payments/{paymentId}")
	public ResponseEntity<PaymentResponseDto> findById(@PathVariable Long paymentId) {
		return ResponseEntity.ok(paymentService.findById(paymentId));
	}

	@PostMapping("/v1/payments")
	public ResponseEntity<Void> purchase(@Valid @RequestBody CreatePaymentRequestDto dto) {
		paymentService.purchaseItem(dto.getItemId(), dto.getIssuedCouponId());
		return ResponseEntity.ok().build();
	}
}
