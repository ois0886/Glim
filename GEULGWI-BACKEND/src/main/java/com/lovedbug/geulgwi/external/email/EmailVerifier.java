package com.lovedbug.geulgwi.external.email;

import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.exception.EmailAlreadyVerifiedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerifier {

    private final MemberRepository memberRepository;
    private final EmailSender emailSender;

    public String sendVerificationCode(String email) {

        if (memberRepository.existsByEmail(email)){
            throw new EmailAlreadyVerifiedException("이미 가입된 이메일입니다.");
        }

        String verificationCode = generateVerificationCode();

        emailSender.sendVerificationEmail(email, verificationCode);

        return verificationCode;
    }

    private String generateVerificationCode(){

        Random random = new Random();

        return String.format("%06d", random.nextInt(1000000));
    }
}
