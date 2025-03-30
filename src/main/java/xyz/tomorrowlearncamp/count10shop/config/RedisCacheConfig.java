package xyz.tomorrowlearncamp.count10shop.config;

import java.util.Objects;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;

@Configuration
@EnableCaching
public class RedisCacheConfig {

	@Bean
	public LockProvider lockProvider(RedisTemplate<String, String> redisTemplate) {
		return new RedisLockProvider(Objects.requireNonNull(redisTemplate.getConnectionFactory()));
	}
}
