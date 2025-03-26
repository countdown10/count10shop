package xyz.tomorrowlearncamp.count10shop.domain.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotFoundUserException extends ResponseStatusException {
  public NotFoundUserException() { super(HttpStatus.NOT_FOUND); }
}
