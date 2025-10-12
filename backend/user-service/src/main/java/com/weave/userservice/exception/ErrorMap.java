package com.weave.userservice.exception;

import com.weave.common.exception.ErrorMapInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMap implements ErrorMapInterface {

    // User 2XXX
    USER_ALREADY_EXIST_ERROR(HttpStatus.CONFLICT, 2000, "UserAlreadyExistError", "이미 가입된 유저입니다: $s"),
    NICKNAME_ALREADY_EXIST_ERROR(HttpStatus.CONFLICT, 2001, "NicknameAlreadyExistError", "이미 존재하는 닉네임입니다: %s"),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, 2002, "UnauthorizedError", "관리자 계정만 접근 가능합니다."),
    USER_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, 2003, "UserNotFoundError", "사용자를 찾을 수 없습니다: $s"),
    WRONG_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, 2004, "WrongPasswordError", "잘못된 비밀번호입니다."),
    INVALID_EMAIL_PATTERN_ERROR(HttpStatus.BAD_REQUEST, 2005, "InvalidEmailPatternError", "이메일 형식이 올바르지 않습니다: %s"),
    INVALID_PASSWORD_PATTERN_ERROR(HttpStatus.BAD_REQUEST, 2006, "InvalidPasswordPatternError", "비밀번호 형식이 올바르지 않습니다");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String errorName;
    private final String message;

}
