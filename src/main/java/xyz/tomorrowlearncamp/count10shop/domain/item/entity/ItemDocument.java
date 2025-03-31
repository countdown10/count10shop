package xyz.tomorrowlearncamp.count10shop.domain.item.entity;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Category;
import xyz.tomorrowlearncamp.count10shop.domain.item.enums.Status;

@Getter
@NoArgsConstructor
@Document(indexName = "items", writeTypeHint = WriteTypeHint.FALSE) //Elasticsearch 인덱스를 지정하고, 해당 클래스가 Elasticsearch 도큐먼트로 매핑되도록 정의. writeTypeHint: 도큐먼트 타입 힌트작성 여부 (기본값: true)
@Setting(settingPath = "elastic/item-setting.json")	//Elasticsearch 인덱스의 설정을 정의하는 JSON 파일의 경로 지정
@Mapping(mappingPath = "elastic/item-mapping.json") //Elasticsearch 인덱스의 매핑을 정의하는 JSON 파일의 경로 지정
public class ItemDocument {
	@Id
	private Long id;

	private String itemName;

	private Long price;

	@Enumerated(EnumType.STRING)
	private Category category;

	private String description;

	@Enumerated(EnumType.STRING)
	private Status status;

	private Long quantity;

	@Builder
	public ItemDocument(Long id, String itemName, Long price, Category category, String description, Status status, Long quantity) {
		this.id = id;
		this.itemName = itemName;
		this.price = price;
		this.category = category;
		this.description = description;
		this.status = status;
		this.quantity = quantity;
	}

	public static ItemDocument of(Item item) {
		return ItemDocument.builder()
			.id(item.getId())
			.itemName(item.getItemName())
			.category(item.getCategory())
			.price(item.getPrice())
			.description(item.getDescription())
			.status(item.getStatus())
			.quantity(item.getQuantity())
			.build();
	}
}
