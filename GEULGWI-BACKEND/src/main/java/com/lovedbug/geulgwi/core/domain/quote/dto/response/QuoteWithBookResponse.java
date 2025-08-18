package com.lovedbug.geulgwi.core.domain.quote.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuoteWithBookResponse {

    private Long quoteId;
    private String quoteImageName;
    private Integer quoteViews;
    private Integer page;

    private Long bookId;
    private String bookTitle;
    private String author;
    private String content;
    private String publisher;
    private String bookCoverUrl;

    private boolean isLiked;
    private long likeCount;
}
