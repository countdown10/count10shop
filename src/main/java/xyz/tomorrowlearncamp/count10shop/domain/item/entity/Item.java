package xyz.tomorrowlearncamp.count10shop.domain.item.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.ItemStatus;

@Entity
@Getter
@NoArgsConstructor
public class Item extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String itemName;

	private Long price;

	private String description;

	@Enumerated(EnumType.STRING)
	private ItemStatus status;

	@Builder
	public Item(String itemName, Long price, String description, ItemStatus status) {
		this.itemName = itemName;
		this.price = price;
		this.description = description;
		this.status = status;
	}
}
