package com.lovedbug.geulgwi.core.domain.auth;

import com.lovedbug.geulgwi.core.domain.auth.constant.AuthErrorCode;
import com.lovedbug.geulgwi.core.domain.auth.dto.request.LoginRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.response.JwtResponse;
import com.lovedbug.geulgwi.core.domain.auth.exception.AuthException;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberErrorCode;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberRole;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.member.exception.MemberException;
import com.lovedbug.geulgwi.core.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static com.lovedbug.geulgwi.core.security.JwtUtil.TOKEN_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public JwtResponse login(LoginRequest loginRequest){

        Member member = memberRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new AuthException(AuthErrorCode.EMAIL_NOT_MATCH));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new AuthException(AuthErrorCode.PASSWORD_NOT_MATCH);
        }

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberException(MemberErrorCode.MEMBER_INACTIVE, "memberId = " + member.getMemberId());
        }

        String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getMemberId());
        String refreshToken = jwtUtil.generateRefreshToken(member.getEmail(), member.getMemberId());

        return toJwtResponse(accessToken, refreshToken, member.getEmail(), member.getMemberId());
    }

    public JwtResponse adminLogin(LoginRequest loginRequest){
        try{
            Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "패스워드가 올바르지 않습니다.");
            }

            if (member.getStatus() != MemberStatus.ACTIVE) {
                throw new MemberException(MemberErrorCode.MEMBER_INACTIVE, "memberId = " + member.getMemberId());
            }

            if (member.getRole() != MemberRole.ADMIN) {
                throw new MemberException(MemberErrorCode.ADMIN_ROLE_REQUIRED, "memberId = " + member.getMemberId());
            }

            String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getMemberId());
            String refreshToken = jwtUtil.generateRefreshToken(member.getEmail(), member.getMemberId());

            return toJwtResponse(accessToken, refreshToken, member.getEmail(), member.getMemberId());

        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다.");
        }
    }

    public JwtResponse refresh(String authHeader) {

        String refreshToken = extractTokenFromHeader(authHeader);

        if (!jwtUtil.validateRefreshToken(refreshToken)){
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        String email = jwtUtil.extractEmail(refreshToken);

        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        String newAccessToken = jwtUtil.generateAccessToken(email, member.getMemberId());
        String newRefreshToken =  jwtUtil.generateRefreshToken(email, member.getMemberId());

        return toJwtResponse(newAccessToken, newRefreshToken, email, member.getMemberId());
    }

    private String extractTokenFromHeader(String authHeader){
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)){
            throw new AuthException(AuthErrorCode.INVALID_AUTH_HEADER);
        }
        return authHeader.substring(TOKEN_PREFIX.length());
    }

    private JwtResponse toJwtResponse(String accessToken, String refreshToken, String email, Long memberId){

        return JwtResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .memberEmail(email)
            .memberId(memberId)
            .build();
    }
}
