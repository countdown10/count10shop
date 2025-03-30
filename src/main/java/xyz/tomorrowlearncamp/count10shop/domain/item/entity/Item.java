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

	private String description;

	@Column(nullable = false)
	private Long price;

	@Column(nullable = false)
	private Long quantity;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Status status;

	// @Version
	// private Long version;

	@Builder
	public Item(String itemName, Category category, String description, Long price, Long quantity, Status status) {
		this.itemName = itemName;
		this.category = category;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.status = status;
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
		this.quantity--;

		if (this.quantity == 0) {
			updateStatus(Status.SOLD_OUT);
		}
	}

	public void checkItemStatus() {
		if (this.status.equals(Status.NON_SALE)) {
			throw new InvalidRequestException("해당 상품은 판매하지 않습니다.");
		}

		if (this.status.equals(Status.SOLD_OUT)) {
			throw new InvalidRequestException("재고가 없습니다.");
		}
	}
}
