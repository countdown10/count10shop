package xyz.tomorrowlearncamp.count10shop.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.auth.dto.response.LoginUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.auth.dto.response.SignUpUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.user.entity.User;
import xyz.tomorrowlearncamp.count10shop.domain.user.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserService userService;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public SignUpUserResponseDto signUp(String email, String password) {
		if(userService.existsByEmail(email)){
			throw new InvalidRequestException("Email already exists");
		}

		String encodedPassword = passwordEncoder.encode(password);

		User user = User.builder()
			.email(email)
			.password(encodedPassword)
			.build();

		User saveduser = userService.saveUser(user);

		return SignUpUserResponseDto.builder()
			.id(saveduser.getId())
			.email(saveduser.getEmail())
			.createdAt(saveduser.getCreatedAt())
			.build();
	}

	@Transactional(readOnly = true)
	public LoginUserResponseDto login(String email, String password) {
		User user = userService.findByEmail(email).orElseThrow(() -> new InvalidRequestException("Invalid email address"));

		if(!passwordEncoder.matches(password, user.getPassword())){
			throw new InvalidRequestException("Incorrect Password");
		}

		return LoginUserResponseDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.build();
	}
}
