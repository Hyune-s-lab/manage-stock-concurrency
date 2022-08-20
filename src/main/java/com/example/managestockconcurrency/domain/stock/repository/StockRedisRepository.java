package com.example.managestockconcurrency.domain.stock.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StockRedisRepository {

	private final RedisTemplate<String, String> redisTemplate;

	public Boolean lock(final Long key) {
		return redisTemplate
				.opsForValue()
				.setIfAbsent(key.toString(), "lock", Duration.ofMillis(3_000));
	}

	public Boolean unlock(final Long key) {
		return redisTemplate.delete(key.toString());
	}
}
