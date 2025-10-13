package com.weave.common.exception;

import com.weave.common.dto.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WeaveException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;
    private final ErrorResponse errorResponse;

    public WeaveException(ErrorMapInterface errorMap) {
        this.httpStatus = errorMap.getHttpStatus();
        this.message = errorMap.getMessage();
        this.errorResponse = ErrorResponse.of(
                errorMap.getHttpStatus().value(),
                errorMap.getErrorCode(),
                errorMap.getErrorName(),
                errorMap.getMessage()
        );
    }

    public WeaveException(ErrorMapInterface errorMap, String args) {
        String formattedMessage = String.format(errorMap.getMessage(), args);

        this.httpStatus = errorMap.getHttpStatus();
        this.message = formattedMessage;
        this.errorResponse = ErrorResponse.of(
                errorMap.getHttpStatus().value(),
                errorMap.getErrorCode(),
                errorMap.getErrorName(),
                formattedMessage
        );
    }

}
