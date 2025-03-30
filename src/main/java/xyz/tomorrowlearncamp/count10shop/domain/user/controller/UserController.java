package xyz.tomorrowlearncamp.count10shop.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import xyz.tomorrowlearncamp.count10shop.domain.common.etc.JwtProperties;
import xyz.tomorrowlearncamp.count10shop.domain.common.util.JwtUtil;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.request.DeleteUserRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.request.UpdatePasswordRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.request.UpdateUserRequestDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.response.ReadUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.dto.response.UpdateUserResponseDto;
import xyz.tomorrowlearncamp.count10shop.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
	private final UserService userService;
	private final JwtUtil jwtUtil;

	@GetMapping
	public ResponseEntity<ReadUserResponseDto> getUser(
		@RequestHeader(JwtProperties.HEADER_STRING) String token
	) {
		Long userId = jwtUtil.extractUserId(token);
		return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
	}

	@PatchMapping
	public ResponseEntity<UpdateUserResponseDto> updateMember(
		@RequestHeader(JwtProperties.HEADER_STRING) String token,
		@Validated @RequestBody UpdateUserRequestDto dto
	){
		Long userId = jwtUtil.extractUserId(token);
		return new ResponseEntity<>(userService.updateUser(dto, userId), HttpStatus.OK);
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(
		@RequestHeader(JwtProperties.HEADER_STRING) String token,
		@Validated @RequestBody UpdatePasswordRequestDto dto
	){
		Long userId = jwtUtil.extractUserId(token);
		userService.updateUserPassword(dto, userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteMember(
		@RequestHeader(JwtProperties.HEADER_STRING) String token,
		@Validated @RequestBody DeleteUserRequestDto dto
	){
		Long userId = jwtUtil.extractUserId(token);
		userService.deleteUser(dto, userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}

