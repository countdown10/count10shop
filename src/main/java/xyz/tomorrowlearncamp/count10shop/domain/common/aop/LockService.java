package xyz.tomorrowlearncamp.count10shop.domain.common.aop;

import java.time.Duration;

public interface LockService {
	boolean lock(String key, String value, Duration timeout);
	boolean unlock(String key, String value);
}
