package xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 문성준
 * @Content 쿠폰 생성
 * @Date 25.03.25 18:42
 */
@Getter
@NoArgsConstructor
public class CreateCouponRequest {

    private String name;
    private String content;
    private Integer discountAmount;
    private Integer minOrderPrice;
    private Integer totalQuantity;
    private LocalDateTime expiredAt;
}
