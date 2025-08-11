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

    private static final int BODY_MAX_LENGTH = 100;
    private static final String BODY_TRUNCATE_SUFFIX = "...";
    private static final String TITLE_FORMAT = "%s(p.%d)";

    private String to;
    private FcmNotificationDto notification;
    private FcmDataDto data;

    public static FcmMessageDto createLikedNotification(FcmTokens token, Quote quote){

        String title = String.format(TITLE_FORMAT, quote.getBookTitle(), quote.getPage());

        String body = truncateContent(quote.getContent());

        return FcmMessageDto.builder()
            .to(token.getDeviceToken())
            .notification(
                FcmNotificationDto.toFcmNotification(quote, title, body))
            .data(
                FcmDataDto.toFcmData(quote.getBook(), quote)
            )
            .build();
    }

    private static String truncateContent(String content) {

        if (content.length() > BODY_MAX_LENGTH) {
            return content.substring(0, BODY_MAX_LENGTH - BODY_TRUNCATE_SUFFIX.length()) + BODY_TRUNCATE_SUFFIX;
        }
        return content;
    }
}
