package xyz.tomorrowlearncamp.count10shop.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import xyz.tomorrowlearncamp.count10shop.domain.common.dto.ErrorResponse;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.InvalidRequestException;
import xyz.tomorrowlearncamp.count10shop.domain.common.exception.ServerException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidRequestException.class)
	public ResponseEntity<ErrorResponse> invalidRequestExHandler(InvalidRequestException ex) {
		return getResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<ErrorResponse> serverExHandler(ServerException ex) {
		return getResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
	}

	private ResponseEntity<ErrorResponse> getResponse(HttpStatus status, String message) {
		ErrorResponse errorResponse = new ErrorResponse(status, message);
		return new ResponseEntity<>(errorResponse, status);
	}
}
