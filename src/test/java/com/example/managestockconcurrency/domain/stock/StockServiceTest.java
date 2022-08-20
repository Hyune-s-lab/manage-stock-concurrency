package com.example.managestockconcurrency.domain.stock;

import static org.assertj.core.api.Assertions.*;

import com.example.managestockconcurrency.domain.stock.entiry.Stock;
import com.example.managestockconcurrency.domain.stock.facade.StockLettuceLockFacade;
import com.example.managestockconcurrency.domain.stock.facade.StockOptimisticLockFacade;
import com.example.managestockconcurrency.domain.stock.facade.StockRedissonLockFacade;
import com.example.managestockconcurrency.domain.stock.repository.StockRepository;
import com.example.managestockconcurrency.domain.stock.service.StockService;
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

	@Autowired private StockService stockService;
	@Autowired private StockOptimisticLockFacade stockOptimisticLockFacade;
	@Autowired private StockLettuceLockFacade stockLettuceLockFacade;
	@Autowired private StockRedissonLockFacade stockRedissonLockFacade;

	@Autowired private StockRepository stockRepository;

	private final int threadCount = 300;
	private final long productId = 10L;
	private final long quantity = 1L;
	private final long initQuantity = 300L;

	private ExecutorService executorService;
	private CountDownLatch countDownLatch;

	@BeforeEach
	public void beforeEach() {
		stockRepository.save(new Stock(productId, initQuantity));

		executorService = Executors.newFixedThreadPool(threadCount);
		countDownLatch = new CountDownLatch(threadCount);
	}

	@AfterEach
	public void afterEach() {
		stockRepository.deleteAll();
	}

	@DisplayName("[v1] 재고 감소")
	@Test
	void stock_decreaseV1() {
		// given

		// when
		stockService.decreaseV1(productId, quantity);

		// then
		final long afterQuantity = stockRepository.getByProductId(productId).getQuantity();
		assertThat(afterQuantity).isEqualTo(initQuantity - 1);
	}

	@DisplayName("[v1] 재고 감소 - 동시에 300개 요청")
	@Test
	void stock_decreaseV1_concurrency() throws InterruptedException {
		// given

		// when
		IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
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

	@DisplayName("[v2] 재고 감소 - syncronized")
	@Test
	void stock_decreaseV2() throws InterruptedException {
		// given

		// when
		IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
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

	@DisplayName("[v3] 재고 감소 - pessimistic lock")
	@Test
	void stock_decreaseV3() throws InterruptedException {
		// given

		// when
		IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
					try {
						stockService.decreaseV3(productId, quantity);
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

	@DisplayName("[v4] 재고 감소 - optimistic lock")
	@Test
	void stock_decreaseV4() throws InterruptedException {
		// given

		// when
		IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
					try {
						stockOptimisticLockFacade.decreaseV4(productId, quantity);
					} catch (final InterruptedException ex) {
						throw new RuntimeException(ex);
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

	@DisplayName("[v5] 재고 감소 - lettuce lock")
	@Test
	void stock_decreaseV5() throws InterruptedException {
		// given

		// when
		IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
					try {
						stockLettuceLockFacade.decreaseV5(productId, quantity);
					} catch (final InterruptedException ex) {
						throw new RuntimeException(ex);
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

	@DisplayName("[v6] 재고 감소 - redisson lock")
	@Test
	void stock_decreaseV6() throws InterruptedException {
		// given

		// when
		IntStream.range(0, threadCount).forEach(e -> executorService.submit(() -> {
					try {
						stockRedissonLockFacade.decreaseV6(productId, quantity);
					} catch (final InterruptedException ex) {
						throw new RuntimeException(ex);
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
