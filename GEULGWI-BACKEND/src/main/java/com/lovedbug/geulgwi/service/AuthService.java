package com.lovedbug.geulgwi.service;

import com.lovedbug.geulgwi.dto.request.LoginRequestDto;
import com.lovedbug.geulgwi.dto.request.LogoutRequestDto;
import com.lovedbug.geulgwi.dto.resposne.JwtResponseDto;
import com.lovedbug.geulgwi.entity.Member;
import com.lovedbug.geulgwi.repository.MemberRepository;
import com.lovedbug.geulgwi.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public JwtResponseDto login(LoginRequestDto loginRequest){

        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            String email = authentication.getName();

            Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."));

            if (!member.getEmailVerified()){
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 인증이 완료되지 않았습니다. 이메일을 확인해주세요.");
            }

            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            return toJwtResponse(accessToken, refreshToken, email, "read write");
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인에 실패앴습니다.");
        }
    }

    @Transactional
    public void logout(LogoutRequestDto logoutRequest){

        try{
            if (logoutRequest.getRefreshToken() != null){
                log.info("로그아웃 요청: {}",
                    jwtUtil.extractEmail(logoutRequest.getRefreshToken()));
            }
        }catch (Exception e){
            log.warn("로그아웃 처리 중 토큰 파싱 실패", e);
        }
    }

    public JwtResponseDto refresh(String authHeader){

        String refreshToken = extractTokenFromHeader(authHeader);

        if (!jwtUtil.validateToken(refreshToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(email);
        String newRefreshToken = jwtUtil.generateRefreshToken(email);

        return toJwtResponse(newAccessToken, newRefreshToken, email, null);
    }

    private JwtResponseDto toJwtResponse(String accessToken, String refreshToken, String email, String scope){

        JwtResponseDto.JwtResponseDtoBuilder builder = JwtResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(calculateExpiresIn(accessToken))
            .accessTokenExpires(extractTokenExpiration(accessToken))
            .refreshTokenExpires(extractTokenExpiration(refreshToken))
            .userEmail(email);

        if (scope != null){
            builder.scope(scope);
        }

        return builder.build();
    }

    private Instant extractTokenExpiration(String token){
        return jwtUtil.extractExpiration(token).toInstant();
    }

    private long calculateExpiresIn(String token){

        return ChronoUnit.SECONDS.between(Instant.now(), extractTokenExpiration(token));
    }

    private String extractTokenFromHeader(String authHeader){
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없거나 형식이 잘못되었습니다.");
        }
        return authHeader.substring(7);
    }

}
