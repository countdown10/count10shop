package xyz.tomorrowlearncamp.count10shop.domain.user.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.request.DeleteUserRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.request.UpdatePasswordRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.request.UpdateUserRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.response.ReadUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.response.UpdateUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.entity.User;
import xyz.tomorrowlearncamp.count10shop.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public ReadUserResponseDto getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new InvalidRequestException("찾는 유저가 없습니다."));

		return ReadUserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.createdAt(user.getCreatedAt())
			.modifiedAt(user.getModifiedAt())
			.build();
	}

	@Transactional
	public UpdateUserResponseDto updateUser(UpdateUserRequestDto dto, Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new InvalidRequestException("찾는 유저가 없습니다."));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new InvalidRequestException("Password does not match");
		}

		if (existsByEmail(dto.getEmail())) {
			throw new InvalidRequestException("Email already exists");
		}

		if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
			user.updateEmail(dto.getEmail());
		}

		return UpdateUserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getModifiedAt())
			.build();
	}

	@Transactional
	public void updateUserPassword(UpdatePasswordRequestDto dto, Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new InvalidRequestException("찾는 유저가 없습니다."));

		if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
			throw new InvalidRequestException("password does not match");
		}

		String encodedPassword = passwordEncoder.encode(dto.getNewPassword());

		user.updatePassword(encodedPassword);
	}

	@Transactional
	public void deleteUser(DeleteUserRequestDto dto, Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new InvalidRequestException("찾는 유저가 없습니다."));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new InvalidRequestException("password does not match");
		}

		user.delete();
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}

	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	public boolean existsById(Long id) {
		return userRepository.existsById(id);
	}
}
