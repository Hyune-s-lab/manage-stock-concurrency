package com.example.managestockconcurrency.domain.stock;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {


	@Autowired
	private StockService stockService;

	@Autowired StockRepository stockRepository;

	@BeforeEach
	public void beforeEach() {
		stockRepository.save(new Stock(1L, 100L));
	}

	@AfterEach
	public void afterEach() {
		stockRepository.deleteAll();
	}

	private final long productId = 1L;
	private final long quantity = 1L;

	@DisplayName("[v1] 재고 감소")
	@Test
	void stock_decreaseV1() {
		// given

		// when
		stockService.decreaseV1(productId, quantity);

		// then
		final long afterQuantity = stockRepository.getByProductId(productId).getQuantity();
		assertThat(afterQuantity).isEqualTo(99L);
	}
}
