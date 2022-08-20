package com.example.managestockconcurrency.domain.stock.facade;

import com.example.managestockconcurrency.domain.stock.service.StockService;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StockRedissonLockFacade {

	private final RedissonClient redissonClient;
	private final StockService stockService;

	/**
	 * [v6] 재고 감소 - redisson lock
	 */
	public void decreaseV6(final Long productId, final Long quantity) throws InterruptedException {
		final RLock lock = redissonClient.getLock(productId.toString());

		try {
			if (!lock.tryLock(10, 1, TimeUnit.SECONDS)) {
				log.info("### redisson getLock timeout");
				return;
			}

			// 기본 재고 감소 로직 활용
			stockService.decreaseV1(productId, quantity);
		} finally {
			lock.unlock();
		}
	}
}
