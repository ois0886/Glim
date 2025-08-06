package com.lovedbug.geulgwi.external.fcm.dto.response;

import com.lovedbug.geulgwi.external.fcm.entity.FcmTokens;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmMessageDto {

    private String to;
    private FcmNotificationDto notification;
    private FcmDataDto data;

    public static FcmMessageDto createLikedNotification(FcmTokens token, Quote quote){

        String title = quote.getBookTitle() + "(p." + quote.getPage() + ")";

        String body = quote.getContent().length() > 100
            ? quote.getContent().substring(0, 97) + "..."
            : quote.getContent();

        return FcmMessageDto.builder()
            .to(token.getDeviceToken())
            .notification(
                FcmNotificationDto.toFcmNotification(quote, title, body))
            .data(
                FcmDataDto.toFcmData(quote.getBook(), quote)
            )
            .build();
    }
}
