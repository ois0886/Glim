package com.lovedbug.geulgwi.core.domain.auth;

import com.lovedbug.geulgwi.core.domain.auth.dto.request.EmailVerificationRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.request.LoginRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.request.LogoutRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.response.EmailVerificationResponse;
import com.lovedbug.geulgwi.core.domain.auth.dto.response.JwtResponse;
import com.lovedbug.geulgwi.core.security.JwtUtil;
import com.lovedbug.geulgwi.core.security.annotation.CurrentUser;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import com.lovedbug.geulgwi.external.email.EmailVerifier;
import com.lovedbug.geulgwi.external.fcm.service.FcmTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerifier emailVerifier;
    private final FcmTokenService fcmTokenService;

    @PostMapping("/email-verification-code")
    public ResponseEntity<EmailVerificationResponse> sendVerificationCode(
        @RequestBody EmailVerificationRequest emailVerificationRequest) {

        String verificationCode = emailVerifier.sendVerificationCode(emailVerificationRequest.getEmail());

        return ResponseEntity
            .ok()
            .body(EmailVerificationResponse.builder()
                .message("인증 이메일이 전송되었습니다.")
                .email(emailVerificationRequest.getEmail())
                .verificationCode(verificationCode)
                .build()
            );
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest){

        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(HttpServletRequest request) {

        return ResponseEntity.ok(authService.refresh(request.getHeader(JwtUtil.HEADER_AUTH)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
        @CurrentUser AuthenticatedUser user,
        @RequestBody LogoutRequest logoutRequest){

        fcmTokenService.inActivateToken(user.getMemberId(), logoutRequest.getDeviceId());

        return ResponseEntity.noContent().build();
    }
}
