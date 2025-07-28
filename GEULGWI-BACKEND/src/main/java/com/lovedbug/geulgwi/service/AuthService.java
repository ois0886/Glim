package com.lovedbug.geulgwi.service;

import com.lovedbug.geulgwi.dto.request.LoginRequestDto;
import com.lovedbug.geulgwi.dto.resposne.JwtResponseDto;
import com.lovedbug.geulgwi.dto.resposne.MemberDto;
import com.lovedbug.geulgwi.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import static com.lovedbug.geulgwi.utils.JwtUtil.TOKEN_PREFIX;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final MemberService memberService;

    public JwtResponseDto login(LoginRequestDto loginRequest){

        try{
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            String email = authentication.getName();

            MemberDto member = memberService.findByMemberEmail(email);

            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);
            jwtUtil.generateRefreshToken(email);

            return toJwtResponse(accessToken, refreshToken ,email, member.getMemberId());
        }catch(Exception e){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다.");
        }
    }

    public JwtResponseDto refresh(String authHeader){

        String refreshToken = extractTokenFromHeader(authHeader);

        if (!jwtUtil.validateRefreshToken(refreshToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        String email = jwtUtil.extractEmail(refreshToken);

        MemberDto member = memberService.findByMemberEmail(email);
        String newAccessToken = jwtUtil.generateAccessToken(email, member.getMemberId());
        jwtUtil.generateRefreshToken(email, member.getMemberId());

        return toJwtResponse(newAccessToken, null ,email, member.getMemberId());
    }

    private JwtResponseDto toJwtResponse(String accessToken, String refreshToken, String email, Long memberId){

        return JwtResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .memberEmail(email)
            .memberId(memberId)
            .build();
    }

    private String extractTokenFromHeader(String authHeader){
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization 헤더가 없거나 형식이 잘못되었습니다.");
        }
        return authHeader.substring(TOKEN_PREFIX.length());
    }

}
