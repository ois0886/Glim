package com.lovedbug.geulgwi.core.domain.quote.dto.response;

import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuoteResponse {

    private Long quoteId;
    private String content;
    private Integer views;
    private Integer page;

    private boolean isLiked;
    private Long likeCount;

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
}
