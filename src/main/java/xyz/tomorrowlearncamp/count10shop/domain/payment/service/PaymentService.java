package xyz.tomorrowlearncamp.count10shop.domain.payment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.entity.Payment;
import xyz.tomorrowlearncamp.count10shop.domain.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final ItemService itemService;

	public Page<PaymentListResponseDto> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<Payment> payments = paymentRepository.findAll(pageable);

		return payments.map(PaymentListResponseDto::of);
	}

	public PaymentResponseDto findById(Long id) {
		Payment savedPayment = paymentRepository.findByIdOrElseThrow(id);

		return PaymentResponseDto.of(savedPayment);
	}

	@Transactional
	public void purchaseItem(Long itemId) {
		Item savedItem = itemService.findItemByIdOrElseThrow(itemId);

		checkItemStatus(savedItem);

		savedItem.decrementQuantity();

		Payment payment = Payment.builder()
			.itemId(savedItem.getId())
			.itemName(savedItem.getItemName())
			.price(savedItem.getPrice())
			.totalPrice(savedItem.getPrice())
			.build();

		paymentRepository.save(payment);
	}

	private void checkItemStatus(Item savedItem) {
		if (savedItem.getStatus().equals(Status.NON_SALE)) {
			throw new InvalidRequestException("해당 상품은 판매하지 않습니다.");
		}

		if (savedItem.getStatus().equals(Status.SOLD_OUT)) {
			throw new InvalidRequestException("재고가 없습니다.");
		}
	}
}
