package com.weave.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRefreshTokenRequest {

    @NotBlank
    private Long userId;

    @NotBlank
    private String refreshToken;

}
