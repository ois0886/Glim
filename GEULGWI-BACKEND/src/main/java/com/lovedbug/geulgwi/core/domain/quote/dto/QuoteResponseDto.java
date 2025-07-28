package com.lovedbug.geulgwi.core.domain.quote.dto;

import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuoteResponseDto {

    private Long quoteId;
    private String content;
    private Integer views;
    private Integer page;

    public static QuoteResponseDto toResponseDto(Quote quote){
        return QuoteResponseDto.builder()
            .quoteId(quote.getQuoteId())
            .content(quote.getContent())
            .views(quote.getViews())
            .page(quote.getPage())
            .build();
    }
}
