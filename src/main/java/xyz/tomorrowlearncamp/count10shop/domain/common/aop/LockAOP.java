package xyz.tomorrowlearncamp.count10shop.domain.common.aop;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.tomorrowlearncamp.count10shop.domain.common.annotation.RedissonLock;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LockAOP {
	private final AOPTransaction aopTransaction;
	private final LettuceLockService lockService;
	private final RedissonClient redissonClient;

	@Around("execution(* xyz.tomorrowlearncamp.count10shop.domain.payment.service.LettuceLockPaymentService.purchaseItem(..))")
	public Object lockAroundPurchase(ProceedingJoinPoint joinPoint) throws Throwable {
		String key = "item";
		String value = UUID.randomUUID().toString();
		Duration timeout = Duration.ofSeconds(1);
		long start = System.currentTimeMillis();
		long end = start + 5000L;

		/*
		* SpinLock 방식
		* 특정 시간동안 루프를 돌면서 Lock 획득을 시도한다.
		* 특정 시간 이후에도 획득하지 못하면 예외를 던진다.
		* */
		while (System.currentTimeMillis() < end) {
			if (lockService.lock(key, value, timeout)) {
				try {
					return aopTransaction.execute(joinPoint);
				} finally {
					lockService.unlock(key, value);
				}
			}
			/*
			* Thread sleep 없이 루프를 돌 경우 1초당 매우 많은 Lock 획득을 시도한다.
			* 그렇기 때문에 적절한 시간을 파라미터로 넣어서 스래드를 sleep 시킨다.
			* --- 주의사항 ---
			* 파라미터 값이 너무 짧다면 Context-Switching 으로 인한 비용이 더 커진다.
			* */
			Thread.sleep(200L);
		}

		throw new InvalidRequestException("Item is locked");
	}

	@Around("@annotation(redissonLock)")
	public Object redissonLockAroundPurchase(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
		log.info("AOP Start");
		String key = redissonLock.key();
		long waitTime = redissonLock.waitTime();
		long leaseTime = redissonLock.leaseTime();

		// FairLock 을 적용한다. 일반 락의 경우 getLock(key) 를 사용한다.
		RLock fairLock = redissonClient.getFairLock(key);

		try {
			log.info("Lock acquisition started");
			/*
			* Lock 획득 여부를 return value 로 준다. 실패시 false 반환.
			* --- 파라미터 ---
			* 1. Lock 획득 시도 시간
			* 2. Lock 획득 후 유지 시간 (획득 후 네트워크 에러 등으로 Lock 을 반환하지 않아도 유지 시간이 지나면 자동으로 반환한다. 데드락 방지)
			* 3. 1번과 2번 파라미터 값의 시간 단위
			* */
			boolean available = fairLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
			log.info("Lock acquisition finished");

			if (!available) {
				for (int i = 0; i < 3; i++) {
					boolean locked = fairLock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
					if (locked)
						return aopTransaction.execute(joinPoint);
				}
				log.info("Lock acquisition failed");
				throw new InvalidRequestException("Lock acquisition failed");
			}

			return aopTransaction.execute(joinPoint);
		} catch (InterruptedException e) {
			// 락 획득 시도 중 예외가 발생 할 수 있다.
			log.warn("Lock acquisition interrupted");
			throw new InvalidRequestException("Lock acquisition interrupted");
		} finally {
			// aopTransaction.execute(joinPoint) 실행후 return 되기 전에 획득한 Lock 을 반환해야 한다.
			fairLock.unlock();
		}
	}
}
