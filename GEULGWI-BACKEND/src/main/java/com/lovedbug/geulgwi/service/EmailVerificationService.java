package com.lovedbug.geulgwi.service;

import com.lovedbug.geulgwi.exception.EmailAlreadyVerifiedException;
import com.lovedbug.geulgwi.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final MemberRepository memberRepository;
    private final EmailService emailService;

    public String sendVerificationCode(String email) {

        if (memberRepository.existsByEmail(email)){
            throw new EmailAlreadyVerifiedException("이미 가입된 이메일입니다.");
        }

        String verificationCode = generateVerificationCode();

        emailService.sendVerificationEmail(email, verificationCode);

        return verificationCode;
    }

    private String generateVerificationCode(){

        Random random = new Random();

        return String.format("%06d", random.nextInt(1000000));
    }
}
