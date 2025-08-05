package com.lovedbug.geulgwi.core.domain.fcm;

import com.lovedbug.geulgwi.core.domain.fcm.dto.request.DeviceTokenRequest;
import com.lovedbug.geulgwi.core.domain.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.core.domain.fcm.repository.FcmTokenRepository;
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
    public void saveDeviceToken(AuthenticatedUser user, DeviceTokenRequest deviceTokenRequest) {

        Member member = memberRepository.findById(user.getMemberId())
            .orElseThrow(() -> new IllegalStateException("회원이 존재하지 않습니다."));

        fcmTokenRepository.findByMemberAndDeviceId(member, deviceTokenRequest.getDeviceId())
            .ifPresentOrElse(saved -> {
                if (!saved.getDeviceToken().equals(deviceTokenRequest.getDeviceToken())) {
                    saved.updateDeviceToken(deviceTokenRequest.getDeviceToken());
                }
            },
            () -> {
                FcmTokens token = FcmTokens.builder()
                    .member(member)
                    .deviceToken(deviceTokenRequest.getDeviceToken())
                    .deviceType(deviceTokenRequest.getDeviceType())
                    .deviceId(deviceTokenRequest.getDeviceId())
                    .isActive(true)
                    .build();

                fcmTokenRepository.save(token);
            });
    }
}
