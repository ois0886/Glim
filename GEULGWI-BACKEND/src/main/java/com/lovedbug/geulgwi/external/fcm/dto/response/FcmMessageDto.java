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
    private FcmDataDto data;

    public static FcmMessageDto createLikedNotification(FcmTokens token, Quote quote, String nickName){

        return FcmMessageDto.builder()
            .to(token.getDeviceToken())
            .data(
                FcmDataDto.toFcmData(quote.getBook(), quote, nickName)
            )
            .build();
    }
}
