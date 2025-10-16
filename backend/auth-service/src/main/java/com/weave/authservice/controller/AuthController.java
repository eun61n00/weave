package com.weave.authservice.controller;

import com.weave.authservice.dto.request.LoginRequest;
import com.weave.authservice.dto.request.RefreshTokenRequest;
import com.weave.authservice.dto.response.LoginResponse;
import com.weave.authservice.dto.response.AccessTokenResponse;
import com.weave.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로그인", description = "사용자 이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "액세스 토큰 갱신", description = "리프레시 토큰으로 새로운 액세스 토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "액세스 토큰 갱신 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })
    @PostMapping("/update")
    public ResponseEntity<AccessTokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        String newAccessToken = authService.updateAccessToken(request.getRefreshToken());
        AccessTokenResponse response = new AccessTokenResponse(newAccessToken);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그아웃", description = "사용자 로그아웃을 처리하고 리프레시 토큰을 만료시킵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

}
