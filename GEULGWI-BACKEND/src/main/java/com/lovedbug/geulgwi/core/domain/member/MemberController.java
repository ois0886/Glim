package com.lovedbug.geulgwi.core.domain.member;

import com.lovedbug.geulgwi.core.domain.member.dto.MemberDto;
import com.lovedbug.geulgwi.core.domain.member.dto.SignUpRequestDto;
import com.lovedbug.geulgwi.core.domain.member.dto.SignUpResponseDto;
import com.lovedbug.geulgwi.core.domain.member.dto.UpdateRequestDto;
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
    public ResponseEntity<SignUpResponseDto> signup(@RequestBody SignUpRequestDto signUpRequest){

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .build()
            .toUri();

        return ResponseEntity
            .created(location)
            .body(memberService.registerMember(signUpRequest));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> getMemberById(@PathVariable("memberId") Long memberId){

        return ResponseEntity.ok(memberService.findByMemberId(memberId));
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
