package com.lovedbug.geulgwi.external.fcm.dto.response;

import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.external.fcm.constant.NotificationType;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmDataDto {

    private String bookId;
    private String quoteId;
    private String bookTitle;
    private Integer page;
    private String quoteText;
    private NotificationType screen;

    public static FcmDataDto toFcmData(Book book, Quote quote){

        return FcmDataDto.builder()
            .bookId(book.getBookId().toString())
            .quoteId(quote.getQuoteId().toString())
            .bookTitle(book.getTitle())
            .page(quote.getPage())
            .quoteText(quote.getContent())
            .screen(NotificationType.LIKE)
            .build();
    }
}
