package com.example.managestockconcurrency.domain.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StockService {

	private final StockRepository stockRepository;

	/**
	 * [v1] 재고 감소
	 */
	@Transactional
	public void decreaseV1(final Long productId, final Long quantity) {
		final Stock stock = stockRepository.getByProductId(productId);
		stock.decrease(quantity);
	}
}
