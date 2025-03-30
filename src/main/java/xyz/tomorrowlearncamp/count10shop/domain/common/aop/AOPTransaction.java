package xyz.tomorrowlearncamp.count10shop.domain.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AOPTransaction {
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
		return joinPoint.proceed();
	}
}
