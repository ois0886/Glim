package com.lovedbug.geulgwi.core.domain.member;

import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.SignUpRequest;
import com.lovedbug.geulgwi.core.domain.member.dto.response.SignUpResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.request.UpdateRequest;
import com.lovedbug.geulgwi.core.security.annotation.CurrentUser;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import com.lovedbug.geulgwi.external.fcm.dto.request.FcmTokenRequestDto;
import com.lovedbug.geulgwi.external.fcm.service.FcmTokenService;
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
    private final FcmTokenService fcmTokenService;

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

    @PutMapping("/me")
    public ResponseEntity<MemberResponse> updateMember(
        @CurrentUser AuthenticatedUser user,
        @RequestPart UpdateRequest updateRequest,
        @RequestPart MultipartFile profileImage) {

        return ResponseEntity.ok(memberService.updateMember(user.getMemberId(), updateRequest, profileImage));
    }

    @PatchMapping("/me/status")
    public ResponseEntity<MemberResponse> deleteMember(
        @CurrentUser AuthenticatedUser user,
        @RequestBody FcmTokenRequestDto fcmTokenRequestDto){

        fcmTokenService.inActivateToken(user.getMemberId(), fcmTokenRequestDto.getDeviceId());

        return ResponseEntity.ok(memberService.softDeleteMember(user.getMemberId()));
    }
}
