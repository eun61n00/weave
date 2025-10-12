package com.weave.authservice.exception;

import com.weave.common.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(WeaveAuthException.class)
    public ResponseEntity<Object> handleWeaveUserException(WeaveAuthException e) {
        log.error("[WeaveUserException] HttpStatus: {}, Message: {}", e.getHttpStatus(), e.getMessage());
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(e.getErrorResponse());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        log.error("[AccessDeniedException] {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.FORBIDDEN.value(), 4030, "ACCESS_DENIED", "You do not have permission to access this resource.");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error("[Spring MVC Exception] HttpStatus: {}, Message: {}", statusCode, ex.getMessage());

        // @Valid 에러
        if (ex instanceof MethodArgumentNotValidException) {
            String errorMessage = ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
                    .map(fieldError -> String.format("'%s': %s", fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.joining(", "));
            
            ErrorResponse errorResponse = ErrorResponse.of(statusCode.value(), 4001, "INVALID_INPUT_VALUE", errorMessage);
            return ResponseEntity.status(statusCode).body(errorResponse);
        }

        // 그 외 Spring 기본 예외
        ErrorResponse errorResponse = ErrorResponse.of(statusCode.value(), statusCode.value() * 10, ex.getClass().getSimpleName(), ex.getLocalizedMessage());
        return ResponseEntity.status(statusCode).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e) {
        log.error("[Unhandled Exception] An unexpected error occurred", e);
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), 9999, e.getClass().getSimpleName(), "An internal server error has occurred.");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    } 
}
