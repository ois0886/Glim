package com.lovedbug.geulgwi.external.fcm.service;

import com.lovedbug.geulgwi.core.domain.member.constant.MemberErrorCode;
import com.lovedbug.geulgwi.core.domain.member.exception.MemberException;
import com.lovedbug.geulgwi.external.fcm.constant.SaveResult;
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
    public SaveResult saveFcmToken(AuthenticatedUser user, FcmTokenRequestDto fcmTokenRequest) {

        Member member = memberRepository.findById(user.getMemberId())
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "memberId = " + user.getMemberId()));

        return fcmTokenRepository.findByMemberAndDeviceId(member, fcmTokenRequest.getDeviceId())
            .map(saved -> {
                updateFcmToken(saved, fcmTokenRequest);
                return SaveResult.UPDATED;
            })
            .orElseGet(() -> {
                createdFcmToken(member, fcmTokenRequest);
                return SaveResult.CREATED;
            });
    }

    private void updateFcmToken (FcmTokens savedToken, FcmTokenRequestDto fcmTokenRequest) {

        if (!savedToken.getDeviceToken().equals(fcmTokenRequest.getDeviceToken())){
            savedToken.updateDeviceToken(fcmTokenRequest.getDeviceToken());
            savedToken.updateIsActive(true);
        }
    }

    private void createdFcmToken(Member member, FcmTokenRequestDto fcmTokenRequest) {

        FcmTokens token = FcmTokens.builder()
            .member(member)
            .deviceToken(fcmTokenRequest.getDeviceToken())
            .deviceType(fcmTokenRequest.getDeviceType())
            .deviceId(fcmTokenRequest.getDeviceId())
            .isActive(true)
            .build();

        fcmTokenRepository.save(token);
    }

    @Transactional
    public void inActivateToken(Long memberId, String deviceId) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND, "memberId = " + memberId));

        fcmTokenRepository.findByMemberAndDeviceId(member, deviceId)
            .ifPresent(
                token -> token.updateIsActive(false)
            );
    }
}
