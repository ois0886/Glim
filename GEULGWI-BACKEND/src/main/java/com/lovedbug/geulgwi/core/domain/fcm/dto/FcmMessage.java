package com.lovedbug.geulgwi.core.domain.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmMessage {

    private String to;
    private FcmNotification notification;
    private FcmData data;
}
