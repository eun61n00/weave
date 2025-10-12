package com.weave.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorMapInterface {

    HttpStatus getHttpStatus();
    int getErrorCode();
    String getErrorName();
    String getMessage();

}
