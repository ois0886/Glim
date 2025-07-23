package com.lovedbug.geulgwi.service;

import com.lovedbug.geulgwi.dto.request.SignUpRequestDto;
import com.lovedbug.geulgwi.entity.Member;
import com.lovedbug.geulgwi.exception.EmailAlreadyVerifiedException;
import com.lovedbug.geulgwi.exception.InvalidVerificationTokenException;
import com.lovedbug.geulgwi.repository.MemberRepository;
import com.lovedbug.geulgwi.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public void createVerificationToken(SignUpRequestDto signUpRequestDto){
        SignUpRequestDto tokenRequest = SignUpRequestDto.builder()
            .email(signUpRequestDto.getEmail())
            .password(signUpRequestDto.getPassword())
            .nickname(signUpRequestDto.getNickname())
            .gender(signUpRequestDto.getGender())
            .birthDate(signUpRequestDto.getBirthDate())
            .build();

        String verificationToken = jwtUtil.generateEmailVerificationToken(tokenRequest);

        emailService.sendVerificationEmail(signUpRequestDto.getEmail(), verificationToken);
    }

    @Transactional
    public void verifyEmailAndCreateMember(String token){

        if (!jwtUtil.validateEmailVerificationToken(token)){
            throw new InvalidVerificationTokenException("유효하지 않거나 만료된 인증 토큰입니다.");
        }

        SignUpRequestDto signUpInfo = jwtUtil.extractSignUpInfo(token);

        if (memberRepository.existsByEmail(signUpInfo.getEmail())){
            throw new EmailAlreadyVerifiedException("이미 가입된 이메일입니다.");
        }

        Member member = Member.builder()
            .email(signUpInfo.getEmail())
            .password(passwordEncoder.encode(signUpInfo.getPassword()))
            .nickname(signUpInfo.getNickname())
            .gender(signUpInfo.getGender())
            .birthDate(signUpInfo.getBirthDate())
            .emailVerified(true)
            .build();

        memberRepository.save(member);

        emailService.sendWelcomeEmail(member.getEmail(), member.getNickname());
    }

    public void resendVerificationEmail(SignUpRequestDto signUpRequestDto){

        if (memberRepository.existsByEmail(signUpRequestDto.getEmail())){
            throw new EmailAlreadyVerifiedException("이미 가입된 이메일 입니다.");
        }

        createVerificationToken(signUpRequestDto);
    }
}
