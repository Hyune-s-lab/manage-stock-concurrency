package com.example.managestockconcurrency.domain.stock.repository;

import com.example.managestockconcurrency.domain.stock.entiry.Stock;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface StockOptimisticLockRepository extends JpaRepository<Stock, Long> {

	@Lock(LockModeType.OPTIMISTIC)
	Stock getByProductId(Long productId);
}
