package xyz.tomorrowlearncamp.count10shop.domain.item.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CreateItemRequestDto {
    @NotBlank
    private final String itemName;

    @NotBlank
    private final String category;

    @Max(Long.MAX_VALUE)
    private final Long price;

    private String description;

    @Min(1)
    private final Long quantity;

    @NotBlank
    private final String status;
}