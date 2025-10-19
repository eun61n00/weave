package com.weave.authservice.exception;

import com.weave.common.exception.ErrorMapInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMap implements ErrorMapInterface {

    // Auth 3XXX
    TOKEN_INVALID_ERROR(HttpStatus.UNAUTHORIZED, 3000, "TokenInvalidError", "토큰이 유효하지 않습니다. 다시 로그인해주세요."),
    TOKEN_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, 3001, "TokenExpiredError", "토큰의 유효기간이 만료되었습니다."),
    REFRESH_TOKEN_NOT_FOUND_ERROR(HttpStatus.UNAUTHORIZED, 3002, "RefreshTokenNotFoundError", "로그아웃된 사용자입니다. 다시 로그인해주세요.");


    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String errorName;
    private final String message;

}