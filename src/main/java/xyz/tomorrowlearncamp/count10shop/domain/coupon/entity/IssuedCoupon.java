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
@Table(name = "issued_coupon")
public class IssuedCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private IssuedCouponStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime usedAt;

    public void use() {
        this.status = IssuedCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    @Builder
    public IssuedCoupon(Long id, Long userId, Coupon coupon, IssuedCouponStatus status, LocalDateTime issuedAt, LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.coupon = coupon;
        this.status = status;
        this.issuedAt = issuedAt;
        this.usedAt = usedAt;
    }
}
