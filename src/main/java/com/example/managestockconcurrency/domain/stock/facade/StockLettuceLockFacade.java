package com.example.managestockconcurrency.domain.stock.facade;

import com.example.managestockconcurrency.domain.stock.repository.StockRedisRepository;
import com.example.managestockconcurrency.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StockLettuceLockFacade {

	private final StockRedisRepository stockRedisRepository;
	private final StockService stockService;

	/**
	 * [v5] 재고 감소 - lettuce lock
	 */
	public void decreaseV5(final Long productId, final Long quantity) throws InterruptedException {
		while (!stockRedisRepository.lock(productId)) {
			Thread.sleep(100);
		}

		try {
			// 기본 재고 감소 로직 활용
			stockService.decreaseV1(productId, quantity);
		} finally {
			stockRedisRepository.unlock(productId);
		}
	}
}
