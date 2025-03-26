package xyz.tomorrowlearncamp.count10shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.service.ItemService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.repository.PaymentRepository;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.NoLockPaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.OptimisticLockPaymentService;
import xyz.tomorrowlearncamp.count10shop.domain.payment.service.PaymentService;

@Configuration
@RequiredArgsConstructor
public class SpringConfig {
	private final PaymentRepository paymentRepository;
	private final ItemService itemService;

	@Bean
	public PaymentService paymentService() {
		return new OptimisticLockPaymentService();
		// return new NoLockPaymentService(paymentRepository, itemService);
	}
}
