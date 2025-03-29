package xyz.tomorrowlearncamp.count10shop.config;

import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;
import xyz.tomorrowlearncamp.count10shop.domain.common.aop.LettuceLockService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.repository.PaymentRepository;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.PaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.PessimisticLockPaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.RedissonAOPLockPaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.RedissonLockPaymentService;

@Configuration
@RequiredArgsConstructor
public class PaymentConfig {
	private final PaymentRepository paymentRepository;
	private final ItemService itemService;
	private final ItemRepository itemRepository;
	private final LettuceLockService lettuceLockService;
	private final RedissonClient redissonClient;
	private final CouponService couponService;

	@Bean
	public PaymentService paymentService() {
		// return new RedissonLockPaymentService(paymentRepository, itemService, itemRepository, redissonClient);
		// return new RedissonAOPLockPaymentService(paymentRepository, itemService);
		// return new LettuceLockPaymentService(paymentRepository, itemService);
		return new PessimisticLockPaymentService(paymentRepository, itemRepository, couponService);
		// return new OptimisticLockPaymentService(paymentRepository, itemService, itemRepository);
		// return new NoLockPaymentService(paymentRepository, itemService);
	}
}
