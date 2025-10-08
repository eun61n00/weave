package com.weave.userservice.controller;

import com.weave.userservice.dto.request.LoginRequest;
import com.weave.userservice.dto.request.SignupRequest;
import com.weave.userservice.dto.request.UpdateRefreshTokenRequest;
import com.weave.userservice.dto.response.UserResponse;
import com.weave.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse response = userService.signup(request);
        return ResponseEntity.status(CREATED).body(response);
    }

    @PostMapping("/check-login-credentials")
    public UserResponse checkLoginCredentials(@RequestBody LoginRequest request) {
        UserResponse response = userService.checkLoginCredentials(request);
        return response;
    }

    @PostMapping("/refresh-token")
    public void updateRefreshToken(@RequestBody UpdateRefreshTokenRequest request) {
        userService.updateRefreshToken(request);
    }

}
