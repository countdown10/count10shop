package xyz.tomorrowlearncamp.count10shop.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import xyz.tomorrowlearncamp.count10shop.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);

	Optional<User> findByEmail(String email);
}
