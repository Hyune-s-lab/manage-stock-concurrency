package com.example.managestockconcurrency.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StockService {

	private final StockRepository stockRepository;
	private final StockPessimisticLockRepository stockPessimisticLockRepository;

	/**
	 * [v1] 재고 감소
	 */
	@Transactional
	public void decreaseV1(final Long productId, final Long quantity) {
		final Stock stock = stockRepository.getByProductId(productId);
		stock.decrease(quantity);
	}

	/**
	 * [v2] 재고 감소 - syncronized
	 */
	public synchronized void decreaseV2(final Long productId, final Long quantity) {
		final Stock stock = stockRepository.getByProductId(productId);
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}

	/**
	 * [v3] 재고 감소 - pessimistic lock
	 */
	@Transactional
	public void decreaseV3(final Long productId, final Long quantity) {
		final Stock stock = stockPessimisticLockRepository.getByProductId(productId);
		stock.decrease(quantity);
	}
}
