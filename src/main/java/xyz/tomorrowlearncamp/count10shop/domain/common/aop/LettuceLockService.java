package xyz.tomorrowlearncamp.count10shop.domain.common.aop;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LettuceLockService {
	private final RedisTemplate<String, String> redisTemplate;

	public boolean lock(String key, String value, Duration timeout) {
		return redisTemplate.opsForValue().setIfAbsent(key, value, timeout);
	}

	public boolean unlock(String key, String value) {
		return redisTemplate.opsForValue().get(key).equals(value);
	}
}
