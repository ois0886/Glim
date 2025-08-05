package com.lovedbug.geulgwi.core.domain.fcm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceTokenRequest {

    private String deviceToken;

    private String deviceType;

    private String deviceId;
}
