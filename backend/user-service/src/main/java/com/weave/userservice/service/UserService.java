package com.weave.userservice.service;

import com.weave.common.exception.WeaveException;
import com.weave.userservice.dto.request.LoginRequest;
import com.weave.userservice.dto.request.SignupRequest;
import com.weave.userservice.dto.request.UpdateRefreshTokenRequest;
import com.weave.userservice.dto.response.UserResponse;
import com.weave.userservice.entity.User;
import com.weave.userservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.weave.common.exception.ErrorMap.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse signup(SignupRequest request) {
        validateSignupRequest(request);
        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );
        return UserResponse.of(userRepository.save(user));
    }

    public UserResponse checkLoginCredentials(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new WeaveException(USER_NOT_FOUND_ERROR));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new WeaveException(WRONG_PASSWORD_ERROR);
        }
        return UserResponse.of(user);
    }

    @Transactional
    public void updateRefreshToken(UpdateRefreshTokenRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new WeaveException(USER_NOT_FOUND_ERROR));
        user.setRefreshToken(request.getRefreshToken());
        userRepository.save(user);
    }

    private void validateSignupRequest(SignupRequest request) {
        // validate email pattern
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; // XXX@XXX.XXX
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(request.getEmail());
        if (!m.matches()) throw new WeaveException(EMAIL_PATTERN_ERROR);

        // validate password pattern
        regex = "^(?=.*[0-9])(?=.*[A-Za-z]).{8,30}$"; // 영문, 숫자를 포함한 8 - 30자리
        p = Pattern.compile(regex);
        m = p.matcher(request.getPassword());
        if (!m.matches()) throw new WeaveException(PASSWORD_PATTERN_ERROR);

        // validate nickname duplication
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new WeaveException(NICKNAME_ALREADY_EXIST_ERROR);
        }
    }

}
