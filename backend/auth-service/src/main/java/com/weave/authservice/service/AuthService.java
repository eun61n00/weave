package com.weave.authservice.service;

import com.weave.authservice.dto.request.LoginRequest;
import com.weave.authservice.dto.request.UpdateRefreshTokenRequest;
import com.weave.authservice.dto.response.LoginResponse;
import com.weave.authservice.dto.response.UserResponse;
import com.weave.authservice.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.transaction.Transactional;

@FeignClient(name = "user-service")
interface UserServiceClient {

    @PostMapping("/api/v1/users/check-login-credentials")
    UserResponse checkLoginCredentials(@RequestBody LoginRequest request);

    @PostMapping("/api/v1/users/refresh-token")
    void updateRefreshToken(@RequestBody UpdateRefreshTokenRequest request);

}

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtProvider jwtProvider;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // validate user
        UserResponse user = userServiceClient.checkLoginCredentials(request);

        // generate token
        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getEmail());

        // update token
        UpdateRefreshTokenRequest updateRefreshTokenRequest = new UpdateRefreshTokenRequest(user.getId(), refreshToken);
        userServiceClient.updateRefreshToken(updateRefreshTokenRequest);

        return new LoginResponse(accessToken, refreshToken);
    }

}
