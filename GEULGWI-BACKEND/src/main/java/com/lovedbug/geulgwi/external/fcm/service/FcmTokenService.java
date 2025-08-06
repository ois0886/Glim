package com.lovedbug.geulgwi.external.fcm.service;

import com.lovedbug.geulgwi.external.fcm.dto.request.FcmTokenRequestDto;
import com.lovedbug.geulgwi.external.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.external.fcm.repository.FcmTokenRepository;
import com.lovedbug.geulgwi.core.domain.member.Member;
import com.lovedbug.geulgwi.core.domain.member.MemberRepository;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final MemberRepository memberRepository;
    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveFcmToken(AuthenticatedUser user, FcmTokenRequestDto fcmTokenRequest) {

        Member member = memberRepository.findById(user.getMemberId())
            .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        fcmTokenRepository.findByMemberAndDeviceId(member, fcmTokenRequest.getDeviceId())
            .ifPresentOrElse(saved -> {
                if (!saved.getDeviceToken().equals(fcmTokenRequest.getDeviceToken())) {
                    saved.updateDeviceToken(fcmTokenRequest.getDeviceToken());
                    saved.updateIsActive(true);
                }
            },
            () -> {
                FcmTokens token = FcmTokens.builder()
                    .member(member)
                    .deviceToken(fcmTokenRequest.getDeviceToken())
                    .deviceType(fcmTokenRequest.getDeviceType())
                    .deviceId(fcmTokenRequest.getDeviceId())
                    .isActive(true)
                    .build();

                fcmTokenRepository.save(token);
            });
    }

    @Transactional
    public void inActivateToken(Long memberId, String deviceId) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new IllegalArgumentException("존재해자 않는 사용자입니다"));

        fcmTokenRepository.findByMemberAndDeviceId(member, deviceId)
            .ifPresent(
                token -> token.updateIsActive(false)
            );
    }
}
