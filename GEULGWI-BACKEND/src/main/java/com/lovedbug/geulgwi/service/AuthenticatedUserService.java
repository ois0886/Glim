package com.lovedbug.geulgwi.service;

import com.lovedbug.geulgwi.dto.resposne.AuthenticatedUser;
import com.lovedbug.geulgwi.entity.Member;
import com.lovedbug.geulgwi.repository.MemberRepository;
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
