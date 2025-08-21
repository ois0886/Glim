package com.lovedbug.geulgwi.core.domain.member;

import com.lovedbug.geulgwi.core.domain.member.constant.MemberErrorCode;
import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.SignUpRequest;
import com.lovedbug.geulgwi.core.domain.member.dto.response.SignUpResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.UpdateRequest;
import com.lovedbug.geulgwi.core.domain.member.exception.MemberException;
import com.lovedbug.geulgwi.core.domain.member.mapper.MemberMapper;
import com.lovedbug.geulgwi.external.email.EmailSender;
import com.lovedbug.geulgwi.external.image.ImageMetaData;
import com.lovedbug.geulgwi.external.image.handler.ImageHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final ImageHandler imageHandler;

    @Transactional
    public SignUpResponse registerMember(SignUpRequest signUpRequest, MultipartFile profileImage){

        if (memberRepository.existsByEmail(signUpRequest.getEmail())){
            throw new MemberException(MemberErrorCode.EMAIL_DUPLICATE, "email = " + signUpRequest.getEmail());
        }

        if (memberRepository.existsByNickname(signUpRequest.getNickname())){
            throw new MemberException(MemberErrorCode.NICKNAME_DUPLICATE, "nickname = " + signUpRequest.getNickname());
        }

        String profileUrl = savedProfileUrl(profileImage);

        Member reigisterMember = Member.builder()
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .nickname(signUpRequest.getNickname())
            .gender(signUpRequest.getGender())
            .birthDate(signUpRequest.getBirthDate())
            .profileUrl(profileUrl)
            .build();

        memberRepository.save(reigisterMember);

        emailSender.sendWelcomeEmail(reigisterMember.getEmail(), reigisterMember.getNickname());

        return SignUpResponse.builder()
            .email(signUpRequest.getEmail())
            .nickname(signUpRequest.getNickname())
            .message("회원가입이 완료되었습니다.")
            .build();
    }

    public String savedProfileUrl(MultipartFile profileImage) {

        if (profileImage == null || profileImage.isEmpty()){
            throw new MemberException(MemberErrorCode.PROFILE_IMAGE_NOT_FOUND, "프로필 이미지가 존재하지 않습니다.");
        }

        ImageMetaData imageMetaData = imageHandler.saveImage(profileImage);
        return imageMetaData.imageName();
    }

    public MemberResponse findByMemberId(Long memberId){

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "memberId = " + memberId));

        return MemberMapper.toMemberDto(member);
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, UpdateRequest updateRequest){

        Member existingMember = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "memberId = " + memberId));

        existingMember.updateFromRequest(updateRequest, memberRepository, passwordEncoder);

        return MemberMapper.toMemberDto(existingMember);
    }

    @Transactional
    public MemberResponse updateMember(Long memberId, UpdateRequest updateRequest, MultipartFile profileImage){

        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "memberId = " + memberId));

        String profileUrl = savedProfileUrl(profileImage);

        existingMember.updateFromRequest(updateRequest, memberRepository, passwordEncoder);
        existingMember.changeProfileUrl(profileUrl);

        return MemberMapper.toMemberDto(existingMember);
    }

    @Transactional
    public MemberResponse softDeleteMember(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "memberId = " + memberId));

        member.changeStatus(MemberStatus.INACTIVE);
        return MemberMapper.toMemberDto(member);
    }
}
