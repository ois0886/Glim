package com.lovedbug.geulgwi.controller;

import com.lovedbug.geulgwi.dto.request.EmailVerificationRequestDto;
import com.lovedbug.geulgwi.dto.request.LoginRequestDto;
import com.lovedbug.geulgwi.dto.resposne.EmailVerificationResponseDto;
import com.lovedbug.geulgwi.service.AuthService;
import com.lovedbug.geulgwi.dto.resposne.JwtResponseDto;
import com.lovedbug.geulgwi.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/email-verification-code")
    public ResponseEntity<EmailVerificationResponseDto> sendVerificationCode(
        @RequestBody EmailVerificationRequestDto request) {

        String verificationCode = emailVerificationService.sendVerificationCode(request.getEmail());

        return ResponseEntity
            .ok()
            .body(EmailVerificationResponseDto.builder()
                .message("인증 이메일이 전송되었습니다.")
                .email(request.getEmail())
                .verificationCode(verificationCode)
                .build()
            );
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequest){

        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(@RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(authService.refresh(authHeader));
    }
}
