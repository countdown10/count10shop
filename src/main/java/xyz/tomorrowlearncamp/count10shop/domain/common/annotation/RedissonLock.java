package xyz.tomorrowlearncamp.count10shop.domain.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {
	String key();

	// Lock 을 기다리는 시간
	long waitTime() default 20L;

	// Lock 을 획득 후 반환하는 시간
	long leaseTime() default 10L;
}
