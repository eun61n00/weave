package com.weave.gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.common.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeoutException;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(-1)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Global Error Handler caught an exception: {}", ex.getMessage());

        HttpStatus status;
        String errorCode;
        String message;

        if (ex instanceof ResponseStatusException responseStatusException) {
            status = (HttpStatus) responseStatusException.getStatusCode();
            errorCode = status.name();
            if (status == HttpStatus.NOT_FOUND) {
                errorCode = "ROUTE_NOT_FOUND";
                message = "요청하신 API를 찾을 수 없습니다.";
                log.error("[ROUTE_NOT_FOUND] Route not found for the request: {}", exchange.getRequest().getURI());
            } else if (status == HttpStatus.SERVICE_UNAVAILABLE) {
                errorCode = "SERVICE_UNAVAILABLE";
                message = "현재 해당 서비스가 점검 중이거나 사용할 수 없습니다. 잠시 후 다시 시도해주세요.";
                log.error("[SERVICE_UNAVAILABLE] Service unavailable for the request: {}", exchange.getRequest().getURI());
            }else {
                message = switch (status) {
                    case UNAUTHORIZED -> "인증 정보가 유효하지 않습니다. 다시 로그인해주세요.";
                    case FORBIDDEN -> "해당 기능에 접근할 권한이 없습니다.";
                    default -> responseStatusException.getReason();
                };
                log.error("[ResponseStatusException] Status: {}, Reason: {}", status, message);
            }
        } else if (ex.getCause() instanceof TimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            errorCode = "GATEWAY_TIMEOUT";
            message = "서버 응답이 늦어지고 있습니다. 잠시 후 다시 시도해주세요.";
            log.error("[TimeoutException] Response from downstream service timed out.");

        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = "INTERNAL_SERVER_ERROR";
            message = "서버 내부에 예기치 못한 오류가 발생했습니다. 관리자에게 문의해주세요.";
            log.error("[Unhandled Exception] An unexpected error occurred", ex);
        }

        ErrorResponse errorResponse = ErrorResponse.of(status.value(), 9990, errorCode, message);
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("Error while serializing ErrorResponse", e);
            return Mono.empty();
        }
    }
}
