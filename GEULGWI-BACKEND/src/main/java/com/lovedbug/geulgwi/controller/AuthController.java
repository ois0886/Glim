package com.lovedbug.geulgwi.controller;

import com.lovedbug.geulgwi.dto.request.EmailVerificationRequestDto;
import com.lovedbug.geulgwi.dto.request.LoginRequestDto;
import com.lovedbug.geulgwi.dto.request.LogoutRequestDto;
import com.lovedbug.geulgwi.dto.request.SignUpRequestDto;
import com.lovedbug.geulgwi.dto.resposne.EmailVerificationResponseDto;
import com.lovedbug.geulgwi.service.AuthService;
import com.lovedbug.geulgwi.dto.resposne.JwtResponseDto;
import com.lovedbug.geulgwi.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/verify-email")
    public ResponseEntity<EmailVerificationResponseDto>verifyEmail(
        @Validated @RequestBody EmailVerificationRequestDto requestDto) {

        emailVerificationService.verifyEmailAndCreateMember(requestDto.getToken());

        EmailVerificationResponseDto responseDto = EmailVerificationResponseDto.builder()
            .token(requestDto.getToken())
            .verified(true)
            .message("이메일 인증이 완료되었습니다. 이제 로그인 할 수 있습니다.")
            .build();

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(
        @Validated @RequestBody SignUpRequestDto requestDto) {

        emailVerificationService.resendVerificationEmail(requestDto);

        return ResponseEntity.ok(Map.of("message", "인증 이메일이 재전송 되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequest){

        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody LogoutRequestDto logoutRequest) {

        authService.logout(logoutRequest);
        return ResponseEntity.ok(Map.of("message", "로그아웃이 완료되었습니다."));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(authService.refresh(authHeader));
    }
}
