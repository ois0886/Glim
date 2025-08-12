package com.lovedbug.geulgwi.core.domain.member.mapper;

import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;

public final class MemberMapper {
    public static MemberResponse toMemberDto(Member member){

        return MemberResponse.builder()
            .memberId(member.getMemberId())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .birthDate(member.getBirthDate())
            .gender(member.getGender())
            .status(member.getStatus())
            .profileUrl(member.getProfileUrl())
            .build();
    }
}
