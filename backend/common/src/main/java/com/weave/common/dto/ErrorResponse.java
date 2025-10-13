package com.weave.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {

    private final int status;
    @JsonProperty("error_code") private final int errorCode;
    @JsonProperty("error_name")private final String errorName;
    private final String message;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static ErrorResponse of(int statusCode, int errorCode, String errorName, String message) {
        return ErrorResponse.builder()
                .status(statusCode)
                .errorCode(errorCode)
                .errorName(errorName)
                .message(message)
                .build();
    }

}
