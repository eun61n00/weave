package com.weave.authservice.exception;

import com.weave.common.exception.ErrorMapInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMap implements ErrorMapInterface {

    // Auth 3XXX
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, 3000, "UnauthorizedError", "인증 정보가 유효하지 않습니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String errorName;
    private final String message;

}
