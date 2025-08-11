package com.lovedbug.geulgwi.core.domain.member;

import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.SignUpRequest;
import com.lovedbug.geulgwi.core.domain.member.dto.response.SignUpResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.UpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("")
    public ResponseEntity<SignUpResponse> signup(
        @RequestPart SignUpRequest signUpRequest,
        @RequestPart MultipartFile profileImage){

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .build()
            .toUri();

        return ResponseEntity
            .created(location)
            .body(memberService.registerMember(signUpRequest, profileImage));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable("memberId") Long memberId){

        return ResponseEntity.ok(memberService.findByMemberId(memberId));
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
