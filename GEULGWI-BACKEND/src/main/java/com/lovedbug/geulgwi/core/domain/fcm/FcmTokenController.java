package com.lovedbug.geulgwi.core.domain.fcm;

import com.lovedbug.geulgwi.core.domain.fcm.dto.request.DeviceTokenRequest;
import com.lovedbug.geulgwi.core.security.annotation.CurrentUser;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/token")
    public ResponseEntity<Void> saveDeviceToken(
        @CurrentUser AuthenticatedUser user,
        @RequestBody DeviceTokenRequest deviceTokenRequest) {

        fcmTokenService.saveDeviceToken(user, deviceTokenRequest);

        return ResponseEntity.ok().build();
    }
}
