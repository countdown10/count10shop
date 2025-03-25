package xyz.tomorrowlearncamp.count10shop.domain.item.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateItemRequestDto {
    private final String itemName;
    private final String category;
    private final Long price;
    private final String description;
    private final Long quantity;
    private final String status;
}