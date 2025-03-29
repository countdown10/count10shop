package xyz.tomorrowlearncamp.count10shop.domain.payment.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.count10shop.domain.common.annotation.RedissonLock;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.entity.Item;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentListResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.dto.response.PaymentResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.payment.entity.Payment;
import xyz.tomorrowlearncamp.count10shop.domain.payment.repository.PaymentRepository;

@Slf4j
@RequiredArgsConstructor
public class RedissonLockPaymentService implements PaymentService {
	private final PaymentRepository paymentRepository;
	private final ItemService itemService;
	private final ItemRepository itemRepository;
	private final RedissonClient redissonClient;

	public Page<PaymentListResponseDto> findAll(int page, int size) {
		Pageable pageable = PageRequest.of(page - 1, size);
		Page<Payment> payments = paymentRepository.findAll(pageable);

		return payments.map(PaymentListResponseDto::of);
	}

	public PaymentResponseDto findById(Long id) {
		Payment savedPayment = paymentRepository.findByIdOrElseThrow(id);

		return PaymentResponseDto.of(savedPayment);
	}

	@Override
	public PaymentResponseDto purchaseItem(Long itemId, Long issuedCouponId) {
		String key = "Item:" + itemId.toString();
		long waitTime = 10L;
		long leaseTime = 10L;

		RLock fairLock = redissonClient.getFairLock(key);

		try {
			boolean available = fairLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

			if (!available) {
				for (int i = 0; i < 3; i++) {
					boolean locked = fairLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);

					if (locked) {
						return PaymentResponseDto.of(purchaseItemWithLock(itemId));
					}
				}
				log.warn("Lock acquisition failed");
				throw new InvalidRequestException("Lock acquisition failed");
			}
			return PaymentResponseDto.of(purchaseItemWithLock(itemId));
		} catch (InterruptedException e) {
			log.warn("Lock acquisition interrupted");
			throw new InvalidRequestException("Lock acquisition interrupted");
		} finally {
			fairLock.unlock();
		}
	}

	public Payment purchaseItemWithLock(Long itemId) {
		Item savedItem = itemService.findItemByIdOrElseThrow(itemId);

		savedItem.checkItemStatus();
		savedItem.decrementQuantity();
		itemRepository.flush();
		log.info("Item Quantity: {}", savedItem.getQuantity());

		Payment payment = Payment.builder()
			.itemId(savedItem.getId())
			.itemName(savedItem.getItemName())
			.price(savedItem.getPrice())
			.totalPrice(savedItem.getPrice())
			.build();

		return paymentRepository.save(payment);
	}
}
