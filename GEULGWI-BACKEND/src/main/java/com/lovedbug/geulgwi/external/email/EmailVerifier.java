package com.lovedbug.geulgwi.external.email;

import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.external.email.constant.EmailErrorCode;
import com.lovedbug.geulgwi.external.email.exception.EmailException;
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
            throw new EmailException(EmailErrorCode.EMAIL_ALREADY_EXISTS, "email=" + email);
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
