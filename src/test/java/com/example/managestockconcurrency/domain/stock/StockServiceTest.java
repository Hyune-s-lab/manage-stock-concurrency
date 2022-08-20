package com.example.managestockconcurrency.domain.stock;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
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

	@DisplayName("[v1] 재고 감소 - 동시에 100개 요청")
	@Test
	void stock_decreaseV1_concurrency() throws InterruptedException {
		// given
		final int threadCount = 100;
		final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		// when
		IntStream.range(0, 100).forEach(e -> executorService.submit(() -> {
					try {
						stockService.decreaseV1(productId, quantity);
					} finally {
						countDownLatch.countDown();
					}
				}
		));

		countDownLatch.await();

		// then
		final Long afterQuantity = stockRepository.getByProductId(productId).getQuantity();
		System.out.println("### afterQuantity=" + afterQuantity);
		assertThat(afterQuantity).isNotZero();
	}

	@DisplayName("[v2] 재고 감소 - 동시에 100개 요청")
	@Test
	void stock_decreaseV2() throws InterruptedException {
		// given
		final int threadCount = 100;
		final ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

		// when
		IntStream.range(0, 100).forEach(e -> executorService.submit(() -> {
					try {
						stockService.decreaseV2(productId, quantity);
					} finally {
						countDownLatch.countDown();
					}
				}
		));

		countDownLatch.await();

		// then
		final Long afterQuantity = stockRepository.getByProductId(productId).getQuantity();
		System.out.println("### afterQuantity=" + afterQuantity);
		assertThat(afterQuantity).isZero();
	}
}