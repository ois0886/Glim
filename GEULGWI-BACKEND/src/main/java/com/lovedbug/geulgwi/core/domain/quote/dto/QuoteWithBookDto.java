package com.lovedbug.geulgwi.core.domain.quote.dto;

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
