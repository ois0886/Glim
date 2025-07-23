package com.lovedbug.geulgwi.controller;

import com.lovedbug.geulgwi.dto.request.SignUpRequestDto;
import com.lovedbug.geulgwi.dto.request.UpdateRequestDto;
import com.lovedbug.geulgwi.dto.resposne.MemberDto;
import com.lovedbug.geulgwi.dto.resposne.SignUpResponseDto;
import com.lovedbug.geulgwi.service.AuthService;
import com.lovedbug.geulgwi.service.EmailVerificationService;
import com.lovedbug.geulgwi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<SignUpResponseDto> signup(@RequestBody SignUpRequestDto signUpRequest){

        SignUpResponseDto response = memberService.registerMember(signUpRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{email}")
    public ResponseEntity<MemberDto> getMemberByEmail(@PathVariable("email") String email){

        return ResponseEntity.ok(memberService.findByMemberEmail(email));
    }

    @GetMapping("")
    public ResponseEntity<List<MemberDto>> getAllMembers(){

        return ResponseEntity.ok(memberService.findAllMembers());
    }

    @GetMapping("/active/{memberId}")
    public ResponseEntity<MemberDto> getActiveMemberByEmail(@PathVariable Long memberId){

        return ResponseEntity.ok(memberService.getActiveMemberByMemberId(memberId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<MemberDto>> getAllActiveMembers() {

        return ResponseEntity.ok(memberService.getAllActiveMembers());
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<MemberDto> updateMember(
        @PathVariable("memberId") Long memberId,
        @RequestBody UpdateRequestDto updateRequest) {

        return ResponseEntity.ok(memberService.updateMember(memberId, updateRequest));
    }

    @PatchMapping("/{memberId}/status")
    public ResponseEntity<MemberDto> deleteMember(
            @PathVariable Long memberId){

        return ResponseEntity.ok(memberService.softDeleteMember(memberId));
    }
}
