package com.lovedbug.geulgwi.core.domain.member;

import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.SignUpRequest;
import com.lovedbug.geulgwi.core.domain.member.dto.response.SignUpResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.UpdateRequest;
import com.lovedbug.geulgwi.core.domain.member.mapper.MemberMapper;
import com.lovedbug.geulgwi.external.email.EmailSender;
import lombok.RequiredArgsConstructor;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;

    @Transactional
    public SignUpResponse registerMember(SignUpRequest signUpRequest){

        if (memberRepository.existsByEmail(signUpRequest.getEmail())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 이메일 입니다.");
        }

        if (memberRepository.existsByNickname(signUpRequest.getNickname())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        Member reigisterMember = Member.builder()
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .nickname(signUpRequest.getNickname())
            .gender(signUpRequest.getGender())
            .birthDate(signUpRequest.getBirthDate())
            .build();

        memberRepository.save(reigisterMember);

        emailSender.sendWelcomeEmail(reigisterMember.getEmail(), reigisterMember.getNickname());

        return SignUpResponse.builder()
            .email(signUpRequest.getEmail())
            .nickname(signUpRequest.getNickname())
            .message("회원가입이 완료되었습니다.")
            .build();
    }

    public MemberResponse findByMemberId(Long memberId){

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException(
                    "회원이 존재 하지 않습니다. memberId = " + memberId));

        return MemberMapper.toMemberDto(member);
    }

    public MemberResponse findByMemberEmail(String email){

        Member member =  memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(
                        "회원이 존재 하지 않습니다. email = " + email));

        return MemberMapper.toMemberDto(member);
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, UpdateRequest updateRequest){

        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(
                        "수정할 회원이 존재 하지 않습니다, memberId = " + memberId));

        existingMember.updateFromRequest(updateRequest, memberRepository, passwordEncoder);
        return MemberMapper.toMemberDto(existingMember);
    }

    @Transactional
    public MemberResponse softDeleteMember(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(
                        "회원이 존재 하지 않습니다."));

        member.changeStatus(MemberStatus.INACTIVE);
        return MemberMapper.toMemberDto(member);
    }
}
