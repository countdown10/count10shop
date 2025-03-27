package xyz.tomorrowlearncamp.count10shop.domain.payment.service;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.entity.Payment;
import xyz.tomorrowlearncamp.count10shop.domain.payment.repository.PaymentRepository;

@Slf4j
@RequiredArgsConstructor
public class OptimisticLockPaymentService implements PaymentService {
	private final PaymentRepository paymentRepository;
	private final ItemService itemService;
	private final ItemRepository itemRepository;

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
	public PaymentResponseDto purchaseItem(Long itemId) {
		Item savedItem = itemService.findItemByIdOrElseThrow(itemId);

		savedItem.checkItemStatus();
		try {
			savedItem.decrementQuantity();
			itemRepository.flush();
			log.info("Item Quantity: {}", savedItem.getQuantity());
		} catch (OptimisticLockingFailureException e) {
			log.warn("Error");
		}

		Payment payment = Payment.builder()
			.itemId(savedItem.getId())
			.itemName(savedItem.getItemName())
			.price(savedItem.getPrice())
			.totalPrice(savedItem.getPrice())
			.build();

		Payment savedPayment = paymentRepository.save(payment);
		return PaymentResponseDto.of(savedPayment);
	}
}
