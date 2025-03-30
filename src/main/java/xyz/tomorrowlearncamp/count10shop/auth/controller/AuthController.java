package xyz.tomorrowlearncamp.count10shop.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
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
		@Valid @RequestBody SignUpUserRequestDto requestDto
	) {
		SignUpUserResponseDto responseDto = authService.signUp(requestDto.getEmail(), requestDto.getPassword());
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginUserResponseDto> login(
		@Valid @RequestBody LoginUserRequestDto requestDto
	) {
		LoginUserResponseDto responseDto = authService.login(requestDto.getEmail(), requestDto.getPassword());
		JwtToken jwtToken = jwtUtil.generateToken(responseDto.getId(), responseDto.getEmail());

		return ResponseEntity.ok()
			.header(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken.getAccessToken())
			.body(responseDto);
	}
}