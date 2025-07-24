package com.lovedbug.geulgwi.service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.lovedbug.geulgwi.dto.request.SignUpRequestDto;
import com.lovedbug.geulgwi.dto.request.UpdateRequestDto;
import com.lovedbug.geulgwi.dto.resposne.MemberDto;
import com.lovedbug.geulgwi.dto.resposne.SignUpResponseDto;
import com.lovedbug.geulgwi.entity.Member;
import com.lovedbug.geulgwi.enums.MemberStatus;
import com.lovedbug.geulgwi.repository.MemberRepository;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public SignUpResponseDto registerMember(SignUpRequestDto signUpRequest){

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

        Member savedMember = memberRepository.save(reigisterMember);

        emailService.sendWelcomeEmail(savedMember.getEmail(), savedMember.getNickname());

        return SignUpResponseDto.builder()
            .email(signUpRequest.getEmail())
            .nickname(signUpRequest.getNickname())
            .message("회원가입이 완료되었습니다.")
            .build();
    }

    public MemberDto findByMemberId(Long memberId){

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException(
                    "회원이 존재 하지 않습니다. memberId = " + memberId));

        return toMemberDto(member);
    }

    public MemberDto findByMemberEmail(String email){

        Member member =  memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(
                        "회원이 존재 하지 않습니다. email = " + email));

        return toMemberDto(member);
    }

    public List<MemberDto> findAllMembers(){

        return memberRepository.findAll().stream()
                .map(this::toMemberDto)
                .toList();
    }

    public MemberDto getActiveMemberByMemberId(Long memberId){

        Member member = memberRepository.findByMemberIdAndStatus(memberId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new NoSuchElementException(
                        "활성 회원이 존재 하지 않습니다. memberId = " + memberId));

        return toMemberDto(member);
    }

    public List<MemberDto> getAllActiveMembers(){

        return memberRepository.findAllByStatus(MemberStatus.ACTIVE).stream()
                .map(this::toMemberDto)
                .toList();
    }

    @Transactional
    public MemberDto updateMember(Long memberId, UpdateRequestDto updateRequest){

        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(
                        "수정할 회원이 존재 하지 않습니다, memberId = " + memberId));

        existingMember.updateFromRequest(updateRequest, memberRepository, passwordEncoder);
        return toMemberDto(existingMember);
    }

    @Transactional
    public MemberDto softDeleteMember(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NoSuchElementException(
                        "회원이 존재 하지 않습니다."));

        member.changeStatus(MemberStatus.INACTIVE);
        return toMemberDto(member);
    }

    private MemberDto toMemberDto(Member member){

        return MemberDto.builder()
            .memberId(member.getMemberId())
            .email(member.getEmail())
            .password(member.getPassword())
            .nickname(member.getNickname())
            .birthDate(member.getBirthDate())
            .gender(member.getGender())
            .status(member.getStatus())
            .build();
    }

}
