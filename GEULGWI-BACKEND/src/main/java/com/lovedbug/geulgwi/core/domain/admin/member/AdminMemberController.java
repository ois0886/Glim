package com.lovedbug.geulgwi.core.domain.admin.member;

import com.lovedbug.geulgwi.core.domain.member.MemberService;
import com.lovedbug.geulgwi.core.domain.member.dto.request.UpdateRequest;
import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;

    private final AdminMemberService adminMemberService;

    @GetMapping("")
    public ResponseEntity<List<MemberResponse>> getAllMembers(){

        return ResponseEntity.ok(adminMemberService.findAllMembers());
    }

    @GetMapping("/active/{memberId}")
    public ResponseEntity<MemberResponse> getActiveMemberByMemberId(@PathVariable Long memberId){

        return ResponseEntity.ok(adminMemberService.getActiveMemberByMemberId(memberId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<MemberResponse>> getAllActiveMembers() {

        return ResponseEntity.ok(adminMemberService.getAllActiveMembers());
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMember(
        @PathVariable("memberId") Long memberId,
        @RequestBody UpdateRequest updateRequest) {

        return ResponseEntity.ok(memberService.updateMember(memberId, updateRequest));
    }

    @PatchMapping("/{memberId}/status")
    public ResponseEntity<MemberResponse> deleteMember(
        @PathVariable Long memberId){

        return ResponseEntity.ok(memberService.softDeleteMember(memberId));
    }
}
