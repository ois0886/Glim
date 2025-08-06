package com.lovedbug.geulgwi.external.fcm;

import com.lovedbug.geulgwi.external.fcm.dto.request.FcmTokenRequestDto;
import com.lovedbug.geulgwi.external.fcm.service.FcmTokenService;
import com.lovedbug.geulgwi.core.security.annotation.CurrentUser;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/token")
    public ResponseEntity<Void> saveDeviceToken(
        @CurrentUser AuthenticatedUser user,
        @RequestBody FcmTokenRequestDto fcmTokenRequest) {

        fcmTokenService.saveFcmToken(user, fcmTokenRequest);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/token/{deviceId}/status")
    public ResponseEntity<Void> inActivateDeviceToken(
        @CurrentUser AuthenticatedUser user,
        @PathVariable String deviceId
    ) {

        fcmTokenService.inActivateToken(user.getMemberId(), deviceId);

        return ResponseEntity.ok().build();
    }
}
