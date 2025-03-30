package xyz.tomorrowlearncamp.count10shop.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import xyz.tomorrowlearncamp.count10shop.auth.dto.response.LoginUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.auth.dto.response.SignUpUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.user.entity.User;
import xyz.tomorrowlearncamp.count10shop.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserService userService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private AuthService authService;

	private final String email = "test@example.com";
	private final String rawPassword = "password123";
	private final String encodedPassword = "encodedPassword123";

	private User user;

	@BeforeEach
	void setUp() {
		user = User.builder()
			.email(email)
			.password(encodedPassword)
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);
	}

	@Test
	void 회원가입성공() {
		// given
		when(userService.existsByEmail(email)).thenReturn(false);
		when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
		when(userService.saveUser(any(User.class))).thenReturn(user);

		// when
		SignUpUserResponseDto response = authService.signUp(email, rawPassword);

		// then
		assertNotNull(response);
		assertEquals(user.getId(), response.getId());
		assertEquals(email, response.getEmail());
		verify(userService).saveUser(any(User.class));
	}

	@Test
	void 회원가입실패_email이_이미_존재함() {
		// given
		when(userService.existsByEmail(email)).thenReturn(true);

		// when & then
		assertThrows(InvalidRequestException.class, () -> authService.signUp(email, rawPassword));
	}

	@Test
	void  email로_사용자_찾기_비밀번호_일치() {
		// given
		when(userService.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

		// when
		LoginUserResponseDto response = authService.login(email, rawPassword);

		// then
		assertNotNull(response);
		assertEquals(user.getId(), response.getId());
		assertEquals(email, response.getEmail());
	}

	@Test
	void 사용자가_존재하지_않음() {
		// given
		when(userService.findByEmail(email)).thenReturn(Optional.empty());

		// when & then
		assertThrows(InvalidRequestException.class, () -> authService.login(email, rawPassword));
	}

	@Test
	void  사용자_존재하지만_비밀번호_불일치() {
		// given
		when(userService.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);

		// when & then
		assertThrows(InvalidRequestException.class, () -> authService.login(email, rawPassword));
	}
}