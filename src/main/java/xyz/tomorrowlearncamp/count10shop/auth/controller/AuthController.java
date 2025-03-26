package xyz.tomorrowlearncamp.count10shop.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.auth.dto.request.LoginUserRequestDto;
import xyz.tomorrowlearncamp.count10shop.auth.dto.request.SignUpUserRequestDto;
import xyz.tomorrowlearncamp.count10shop.auth.dto.response.LoginUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.auth.dto.response.SignUpUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.auth.service.AuthService;
import xyz.tomorrowlearncamp.count10shop.domain.common.entity.JwtToken;
import xyz.tomorrowlearncamp.count10shop.domain.common.etc.JwtProperties;
import xyz.tomorrowlearncamp.count10shop.domain.common.util.JwtUtil;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;
	private final JwtUtil jwtUtil;

	@PostMapping("/signup")
	public ResponseEntity<SignUpUserResponseDto> signUp(
		@Validated @RequestBody SignUpUserRequestDto requestDto
	) {
		SignUpUserResponseDto responseDto = authService.signUp(
			requestDto.getEmail(),
			requestDto.getPassword()
		);
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginUserResponseDto> login(
		@Validated @RequestBody LoginUserRequestDto requestDto
	) {
		LoginUserResponseDto responseDto = authService.login(requestDto.getEmail(), requestDto.getPassword());
		JwtToken jwtToken = jwtUtil.generateToken(responseDto.getId(), requestDto.getEmail());

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken.getAccessToken());
		httpHeaders.set(JwtProperties.REFRESH_HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken.getRefreshToken());

		return new ResponseEntity<>(responseDto, httpHeaders, HttpStatus.OK);
	}

	@PostMapping("/refresh")
	public ResponseEntity<String> refresh(
		@CookieValue(value = JwtProperties.REFRESH_HEADER_STRING, required = false) String refreshToken
	){
		if(refreshToken == null || refreshToken.isEmpty()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		String token = refreshToken.replace(JwtProperties.TOKEN_PREFIX, "");
		Long id = jwtUtil.extractUserId(token);
		String email = jwtUtil.extractEmail(token);

		JwtToken jwtToken = jwtUtil.generateToken(id, email);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken.getAccessToken());
		httpHeaders.set(JwtProperties.REFRESH_HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken.getRefreshToken());

		return new ResponseEntity<>("Refresh Token", httpHeaders, HttpStatus.OK);
	}

	@PostMapping("/logout")
	public ResponseEntity<String> logout(
		@RequestHeader(value = JwtProperties.HEADER_STRING) String token
	){
		Long id = jwtUtil.extractUserId(token);
		String email = jwtUtil.extractEmail(token);
		JwtToken jwtToken = jwtUtil.generateExpiredToken(id, email);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken.getAccessToken());
		httpHeaders.set(JwtProperties.REFRESH_HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken.getRefreshToken());

		return new ResponseEntity<>("로그아웃 성공", httpHeaders, HttpStatus.OK);
	}
}