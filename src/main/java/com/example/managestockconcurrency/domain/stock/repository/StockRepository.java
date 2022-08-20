package com.example.managestockconcurrency.domain.stock.repository;

import com.example.managestockconcurrency.domain.stock.entiry.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

	Stock getByProductId(Long productId);
}
