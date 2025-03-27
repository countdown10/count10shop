package xyz.tomorrowlearncamp.count10shop.domain.common.aop;

import java.time.Duration;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LockAOP {
	private final LockService lockService;

	// @Around("execution(* xyz.tomorrowlearncamp.count10shop.domain.payment.service.LettuceLockPaymentService.purchaseItem(..))")
	public Object lockBeforePurchase(ProceedingJoinPoint joinPoint) throws Throwable {
		String key = "item";
		String value = UUID.randomUUID().toString();
		Duration timeout = Duration.ofSeconds(1);

		while(true) {
			if (lockService.lock(key, value, timeout)) {
				try {
					return joinPoint.proceed();
				} finally {
					lockService.unlock(key, value);
				}
			}
		}
	}
}
