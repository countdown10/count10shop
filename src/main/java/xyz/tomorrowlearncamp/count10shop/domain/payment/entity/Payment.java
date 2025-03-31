package xyz.tomorrowlearncamp.count10shop.domain.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long itemId;

	@Column(nullable = false)
	private String itemName;

	@Column(nullable = false)
	private Long price;

	// todo: coupon 여부 추가
	// @Column(nullable = false)
	// private String coupon;

	@Column(nullable = false)
	private Long totalPrice;

	@Builder
	public Payment(Long id, Long itemId, String itemName, Long price, Long totalPrice) {
		this.id = id;
		this.itemId = itemId;
		this.itemName = itemName;
		this.price = price;
		this.totalPrice = totalPrice;
	}
}
