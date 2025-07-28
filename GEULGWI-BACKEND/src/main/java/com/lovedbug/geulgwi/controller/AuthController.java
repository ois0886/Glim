package com.lovedbug.geulgwi.controller;

import com.lovedbug.geulgwi.dto.request.EmailVerificationRequestDto;
import com.lovedbug.geulgwi.dto.request.LoginRequestDto;
import com.lovedbug.geulgwi.dto.resposne.EmailVerificationResponseDto;
import com.lovedbug.geulgwi.service.AuthService;
import com.lovedbug.geulgwi.dto.resposne.JwtResponseDto;
import com.lovedbug.geulgwi.service.EmailVerificationService;
import com.lovedbug.geulgwi.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
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
        @RequestBody EmailVerificationRequestDto emailVerificationRequest) {

        String verificationCode = emailVerificationService.sendVerificationCode(emailVerificationRequest.getEmail());

        return ResponseEntity
            .ok()
            .body(EmailVerificationResponseDto.builder()
                .message("인증 이메일이 전송되었습니다.")
                .email(emailVerificationRequest.getEmail())
                .verificationCode(verificationCode)
                .build()
            );
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequest){
        
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponseDto> refresh(HttpServletRequest request) {
        
        String authHeader = request.getHeader(JwtUtil.HEADER_AUTH);
        String token = authHeader.substring(JwtUtil.TOKEN_PREFIX.length()).trim();
        return ResponseEntity.ok(authService.refresh(token));
    }
}
