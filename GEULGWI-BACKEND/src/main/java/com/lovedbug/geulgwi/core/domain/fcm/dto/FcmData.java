package com.lovedbug.geulgwi.core.domain.fcm.dto;

import com.lovedbug.geulgwi.core.domain.fcm.constant.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FcmData {

    private String bookId;
    private String quoteId;
    private String bookTitle;
    private Integer page;
    private String quoteText;
    private NotificationType screen;
}
