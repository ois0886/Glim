package com.lovedbug.geulgwi.core.domain.fcm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FcmNotification {

    private String title;
    private String body;
    private String image;
}
