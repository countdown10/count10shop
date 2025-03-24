package xyz.tomorrowlearncamp.count10shop.domain.common.enums;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    NOT_FOUND_USERS("유저를 찾을 수 없습니다."),
    ;

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }
}
