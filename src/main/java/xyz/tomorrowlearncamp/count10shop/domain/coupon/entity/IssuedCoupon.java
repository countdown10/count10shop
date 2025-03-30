package xyz.tomorrowlearncamp.count10shop.domain.coupon.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.entity.BaseEntity;

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

	@Builder
	public IssuedCoupon(Long userId, Coupon coupon, IssuedCouponStatus status, LocalDateTime issuedAt,
		LocalDateTime usedAt) {
		this.userId = userId;
		this.coupon = coupon;
		this.status = status;
		this.issuedAt = issuedAt;
		this.usedAt = usedAt;
	}

	public void use() {
		this.status = IssuedCouponStatus.USED;
		this.usedAt = LocalDateTime.now();
	}
}
