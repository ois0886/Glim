package com.lovedbug.geulgwi.core.domain.auth;

import com.lovedbug.geulgwi.core.domain.auth.dto.request.LoginRequest;
import com.lovedbug.geulgwi.core.domain.auth.dto.response.JwtResponse;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public JwtResponse login(LoginRequest loginRequest){
        try{
            Member member = memberRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "패스워드가 올바르지 않습니다.");
            }

            if (member.getStatus() != MemberStatus.ACTIVE) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "비활성화된 사용자입니다.");
            }

            String accessToken = jwtUtil.generateAccessToken(member.getEmail(), member.getMemberId());
            String refreshToken = jwtUtil.generateRefreshToken(member.getEmail(), member.getMemberId());

            return toJwtResponse(accessToken, refreshToken, member.getEmail(), member.getMemberId());

        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다.");
        }
    }

    public JwtResponse refresh(String refreshToken) {

        String email = jwtUtil.extractEmail(refreshToken);
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "존재하지 않는 사용자입니다."));

        String newAccessToken = jwtUtil.generateAccessToken(email, member.getMemberId());
        jwtUtil.generateRefreshToken(email, member.getMemberId());

        return toJwtResponse(newAccessToken, null, email, member.getMemberId());
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
