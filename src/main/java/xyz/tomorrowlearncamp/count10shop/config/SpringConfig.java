package xyz.tomorrowlearncamp.count10shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.repository.ItemRepository;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;
import xyz.tomorrowlearncamp.count10shop.domain.common.aop.LettuceLockService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.repository.PaymentRepository;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.LettuceLockPaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.NoLockPaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.OptimisticLockPaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.PaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.PessimisticLockPaymentService;

@Configuration
@RequiredArgsConstructor
public class SpringConfig {
	private final PaymentRepository paymentRepository;
	private final ItemService itemService;
	private final ItemRepository itemRepository;
	private final LettuceLockService lettuceLockService;

	@Bean
	public PaymentService paymentService() {
		// return new LettuceLockPaymentService(paymentRepository, itemService);
		return new PessimisticLockPaymentService(paymentRepository, itemRepository);
		// return new OptimisticLockPaymentService(paymentRepository, itemService, itemRepository);
		// return new NoLockPaymentService(paymentRepository, itemService);
	}
}
