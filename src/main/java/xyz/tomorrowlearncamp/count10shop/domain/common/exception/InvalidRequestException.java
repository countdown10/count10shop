package xyz.tomorrowlearncamp.count10shop.domain.common.exception;

public class InvalidRequestException extends RuntimeException {
	public InvalidRequestException(String message) {
		super(message);
	}
}
