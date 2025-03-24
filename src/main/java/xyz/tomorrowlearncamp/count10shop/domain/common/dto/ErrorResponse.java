package xyz.tomorrowlearncamp.count10shop.domain.common.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {

    private int status;

    private String message;

    public ErrorResponse(HttpStatus status, String message ) {
        this.status = status.value();
        this.message = message;
    }
}
