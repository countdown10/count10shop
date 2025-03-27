package xyz.tomorrowlearncamp.count10shop.domain.coupon.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author 문성준
 * @Content 쿠폰 발급
 * @Date 25.03.25 19:00
 */
@Getter
@NoArgsConstructor
public class IssueCouponRequest {
    private Long userId;

    public static IssueCouponRequest of(Long userId) {
        IssueCouponRequest issueCouponRequest = new IssueCouponRequest();
        issueCouponRequest.userId = userId;
        return issueCouponRequest;
    }
}
