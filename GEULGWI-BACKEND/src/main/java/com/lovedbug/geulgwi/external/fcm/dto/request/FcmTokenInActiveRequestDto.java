package com.lovedbug.geulgwi.external.fcm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FcmTokenInActiveRequestDto {

    private String deviceId;
}
