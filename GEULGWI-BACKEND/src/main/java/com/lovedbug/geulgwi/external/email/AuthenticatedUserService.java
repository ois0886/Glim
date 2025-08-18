package com.lovedbug.geulgwi.external.email;

import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticatedUserService {

    private final MemberRepository memberRepository;

    public AuthenticatedUser getAuthenticatedUser(String email) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return AuthenticatedUser.builder()
            .memberId(member.getMemberId())
            .email(member.getEmail())
            .build();
    }
}
