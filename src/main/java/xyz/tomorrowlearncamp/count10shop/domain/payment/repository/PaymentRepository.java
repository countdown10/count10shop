package xyz.tomorrowlearncamp.count10shop.domain.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	default Payment findByIdOrElseThrow(Long id) {
		return findById(id).orElseThrow(() -> new InvalidRequestException("결제 정보를 찾을 수 없습니다."));
	}
}
