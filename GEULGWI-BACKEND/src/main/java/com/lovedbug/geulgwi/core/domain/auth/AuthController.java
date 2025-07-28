package com.lovedbug.geulgwi.core.domain.auth;

import com.lovedbug.geulgwi.core.domain.auth.dto.EmailVerificationRequestDto;
import com.lovedbug.geulgwi.core.domain.auth.dto.JwtResponseDto;
import com.lovedbug.geulgwi.core.domain.member.dto.LoginRequestDto;
import com.lovedbug.geulgwi.core.domain.auth.dto.EmailVerificationResponseDto;
import com.lovedbug.geulgwi.external.email.EmailVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final EmailVerifier emailVerifier;

    @PostMapping("/email-verification-code")
    public ResponseEntity<EmailVerificationResponseDto> sendVerificationCode(
        @RequestBody EmailVerificationRequestDto emailVerificationRequest) {

        String verificationCode = emailVerifier.sendVerificationCode(emailVerificationRequest.getEmail());

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
    public ResponseEntity<JwtResponseDto> refresh(@RequestHeader("Authorization") String authHeader) {

        return ResponseEntity.ok(authService.refresh(authHeader));
    }
}
