package com.example.managestockconcurrency.domain.stock;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long productId;

	private Long quantity;

	@Version
	private Long version;

	public Stock(final Long productId, final Long quantity) {
		this.productId = productId;
		this.quantity = quantity;
	}

	public void decrease(final Long quantity) {
		if (this.quantity - quantity < 0) {
			throw new RuntimeException("재고 부족");
		}

		this.quantity -= quantity;
	}
}
