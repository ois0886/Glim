package com.lovedbug.geulgwi.dto.resposne;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuoteWithBookDto {

    private Long quoteId;
    private String quoteImageName;
    private Integer quoteViews;
    private Integer page;

    private Long bookId;
    private String bookTitle;
    private String author;
    private String publisher;
    private String bookCoverUrl;
}
