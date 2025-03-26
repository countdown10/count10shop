package xyz.tomorrowlearncamp.count10shop.domain.item.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;

@Getter
@Entity
@NoArgsConstructor
public class Item {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String itemName;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Category category;

	@Column
	private String description;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private Long quantity;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	@Builder
	public Item(String itemName, String category, String description, Long price, Long quantity, String status) {
		this.itemName = itemName;
		this.category = Category.valueOf(category.toUpperCase());
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.status = Status.valueOf(status.toUpperCase());
	}

	public void updateInfo(String itemName, String category, String description, Long price, Long quantity) {
		this.itemName = itemName;
		this.category = Category.valueOf(category.toUpperCase());
		this.description = description;
		this.price = price;
		this.quantity = quantity;
	}

	public void updateStatus(Status status) {
		this.status = status;
	}

	public void decrementQuantity() {
		if (this.quantity <= 0) {
			throw new InvalidRequestException("재고가 없습니다.");
		}

		this.quantity--;

		if (this.quantity == 0) {
			updateStatus(Status.SOLD_OUT);
		}
	}
}
