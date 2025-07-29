package com.lovedbug.geulgwi.core.domain.member;

import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.SignUpRequest;
import com.lovedbug.geulgwi.core.domain.member.dto.response.SignUpResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<SignUpResponse> signup(@RequestBody SignUpRequest signUpRequest){

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .build()
            .toUri();

        return ResponseEntity
            .created(location)
            .body(memberService.registerMember(signUpRequest));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable("memberId") Long memberId){

        return ResponseEntity.ok(memberService.findByMemberId(memberId));
    }

    @GetMapping("")
    public ResponseEntity<List<MemberResponse>> getAllMembers(){

        return ResponseEntity.ok(memberService.findAllMembers());
    }

    @GetMapping("/active/{memberId}")
    public ResponseEntity<MemberResponse> getActiveMemberByEmail(@PathVariable Long memberId){

        return ResponseEntity.ok(memberService.getActiveMemberByMemberId(memberId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<MemberResponse>> getAllActiveMembers() {

        return ResponseEntity.ok(memberService.getAllActiveMembers());
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
