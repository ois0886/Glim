package com.lovedbug.geulgwi.external.fcm.dto.response;

import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class FcmNotificationDto {

    private String title;
    private String body;
    private String image;

    public static FcmNotificationDto toFcmNotification (Quote quote, String title , String body){

        return FcmNotificationDto.builder()
            .title(title)
            .body(body)
            .image(quote.getBook().getCoverUrl())
            .build();
    }
}
