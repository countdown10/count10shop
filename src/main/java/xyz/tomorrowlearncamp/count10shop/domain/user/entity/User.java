package xyz.tomorrowlearncamp.count10shop.domain.user.entity;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.entity.BaseEntity;
import xyz.tomorrowlearncamp.count10shop.domain.user.enums.UserRole;

@Entity
@NoArgsConstructor
@SQLRestriction("deleted = false")
@Getter
@Table(name = "users")
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column
	private String address;

	private UserRole userRole;

	private boolean deleted = false;


	@Builder
	public User(String email, String password, String address) {
		this.email = email;
		this.password = password;
		this.address = address;
		this.userRole = UserRole.ROLE_USER;
	}

	@Builder
	public User(String email, String password) {
		this.email = email;
		this.password = password;
		this.userRole = UserRole.ROLE_USER;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

	public void updateAddress(String address) {
		this.address = address;
	}

	public void delete() {
		this.deleted = true;
	}
}
