package xyz.tomorrowlearncamp.count10shop.domain.coupon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "coupon")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column
    private Integer minOrderPrice;

    @Column
    private Integer discountAmount;

    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer issuedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CouponStatus couponStatus;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Builder
    private Coupon(String name, String content, Integer minOrderPrice, Integer discountAmount,
                   Integer totalQuantity, Integer issuedQuantity, CouponStatus couponStatus,
                   LocalDateTime expiredAt) {
        this.name = name;
        this.content = content;
        this.minOrderPrice = minOrderPrice;
        this.discountAmount = discountAmount;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.couponStatus = couponStatus;
        this.expiredAt = expiredAt;
    }

    public void addIssuedQuantity() {
        this.issuedQuantity += 1;
    }

    public void activate() {
        this.couponStatus = CouponStatus.AVAILABLE;
    }
}
