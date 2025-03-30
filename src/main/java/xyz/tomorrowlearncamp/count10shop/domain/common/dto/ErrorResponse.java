package xyz.tomorrowlearncamp.count10shop.domain.common.dto;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ErrorResponse {

	private int status;

	private String message;

	public ErrorResponse(HttpStatus status, String message) {
		this.status = status.value();
		this.message = message;
	}
}
