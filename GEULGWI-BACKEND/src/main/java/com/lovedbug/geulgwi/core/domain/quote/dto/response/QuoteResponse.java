package com.lovedbug.geulgwi.core.domain.quote.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuoteResponse {

    private Long quoteId;
    private String content;
    private String bookTitle;
    private Integer views;
    private Integer page;

    private boolean isLiked;
    private Long likeCount;
    private LocalDateTime createdAt;

    public static QuoteResponse toResponseDto(Quote quote, boolean isLiked, long likeCounts){
        return QuoteResponse.builder()
            .quoteId(quote.getQuoteId())
            .content(quote.getContent())
            .views(quote.getViews())
            .page(quote.getPage())
            .isLiked(isLiked)
            .likeCount(likeCounts)
            .build();
    }

    public static QuoteResponse toResponseDto(Quote quote, boolean isLiked, long likeCounts, LocalDateTime createdAt){
        return QuoteResponse.builder()
            .quoteId(quote.getQuoteId())
            .content(quote.getContent())
            .bookTitle(quote.getBookTitle())
            .views(quote.getViews())
            .page(quote.getPage())
            .isLiked(isLiked)
            .likeCount(likeCounts)
            .createdAt(createdAt)
            .build();
    }
}
