package com.DreamOfDuck.global.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage()); // ErrorCode에서 메시지를 가져옵니다.
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

