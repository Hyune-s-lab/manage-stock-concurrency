package com.example.managestockconcurrency.domain.stock.facade;

import com.example.managestockconcurrency.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockOptimisticLockFacade {

	private final StockService stockService;

	public void decreaseV4(final Long productId, final Long quantity) throws InterruptedException {
		while (true) {
			try {
				stockService.decreaseV4(productId, quantity);
				break;
			} catch (final Exception ex) {
				log.info("### optimistic lock version 충돌");
				Thread.sleep(50);
			}
		}
	}
}
