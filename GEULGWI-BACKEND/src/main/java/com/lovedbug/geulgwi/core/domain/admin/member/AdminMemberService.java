package com.lovedbug.geulgwi.core.domain.admin.member;

import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.domain.member.constant.MemberStatus;
import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import com.lovedbug.geulgwi.core.domain.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponse> findAllMembers(){

        return memberRepository.findAll().stream()
            .map(MemberMapper::toMemberDto)
            .toList();
    }

    public MemberResponse getActiveMemberByMemberId(Long memberId){

        Member member = memberRepository.findByMemberIdAndStatus(memberId, MemberStatus.ACTIVE)
            .orElseThrow(() -> new NoSuchElementException(
                "활성 회원이 존재 하지 않습니다. memberId = " + memberId));

        return MemberMapper.toMemberDto(member);
    }

    public List<MemberResponse> getAllActiveMembers(){

        return memberRepository.findAllByStatus(MemberStatus.ACTIVE).stream()
            .map(MemberMapper::toMemberDto)
            .toList();
    }
}
