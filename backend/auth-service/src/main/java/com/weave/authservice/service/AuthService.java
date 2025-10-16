package com.weave.authservice.service;

import com.weave.authservice.dto.request.LoginRequest;
import com.weave.authservice.dto.response.LoginResponse;
import com.weave.authservice.dto.response.UserResponse;
import com.weave.authservice.entity.RefreshToken;
import com.weave.authservice.exception.WeaveAuthException;
import com.weave.authservice.repository.RefreshTokenRepository;
import com.weave.authservice.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.transaction.Transactional;

import static com.weave.authservice.exception.ErrorMap.*;

@FeignClient(name = "user-service")
interface UserServiceClient {
    @PostMapping("/api/v1/users/check-login-credentials")
    UserResponse checkLoginCredentials(@RequestBody LoginRequest request);

    @GetMapping("/api/v1/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") String userId);
}

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.debug("=== [login] 시작 ===");
        UserResponse user = userServiceClient.checkLoginCredentials(request);
        log.debug("[login] user-service에서 사용자 정보 조회 완료. userId: {}", user.getId());

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getEmail());
        log.debug("[login] access token, refresh token 생성");

        RefreshToken newRefreshToken = new com.weave.authservice.entity.RefreshToken(user.getId().toString(), refreshToken, jwtProvider.getRefreshTokenValidTime());
        refreshTokenRepository.save(newRefreshToken);
        log.debug("[login] refresh token 저장");

        log.info("[LOGIN] user id: {}", user.getId());
        log.debug("=== [login] 종료 ===");
        return new LoginResponse(accessToken, refreshToken);
    }

    @Transactional
    public String updateAccessToken(String oldRefreshToken) {
        log.debug("=== [updateAccessToken] 시작 ===");

        jwtProvider.validateToken(oldRefreshToken);
        log.debug("[updateAccessToken] refresh 토큰 유효성 검증 완료");

        Long userId = jwtProvider.getUserIdFromToken(oldRefreshToken);
        log.debug("[updateAccessToken] 토큰에서 UserId 추출: {}", userId);

        RefreshToken savedRefreshToken = refreshTokenRepository.findById(userId.toString())
                .orElseThrow(() -> new WeaveAuthException(REFRESH_TOKEN_NOT_FOUND_ERROR));
        log.debug("[updateAccessToken] Redis에서 Refresh Token 조회 완료");

        if (!savedRefreshToken.getToken().equals(oldRefreshToken)) {
            log.warn("[updateAccessToken] 요청된 Refresh Token과 저장된 Token이 불일치. userId: {}", userId);
            throw new WeaveAuthException(TOKEN_INVALID_ERROR);
        }
        log.debug("[updateAccessToken] Redis 토큰과 요청 토큰 일치 확인");

        UserResponse user = userServiceClient.getUserById(userId.toString());
        log.debug("[updateAccessToken] user-service에서 사용자 정보 조회 완료");

        String newAccessToken = jwtProvider.generateAccessToken(userId, user.getEmail());
        log.debug("[updateAccessToken] 새로운 access 토큰 생성 완료");

        log.info("[TOKEN REFRESH] userId: {}", userId);
        log.debug("=== [updateAccessToken] 종료 ===");
        return newAccessToken;
    }

    @Transactional
    public void logout(String refreshToken) {
        log.debug("=== [logout] 시작 ===");

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);
        refreshTokenRepository.deleteById(userId.toString());
        log.debug("[logout] Redis에서 Refresh Token 삭제");

        log.info("[LOGOUT] userId: {}", userId);
        log.debug("=== [logout] 종료 ===");
    }
}
