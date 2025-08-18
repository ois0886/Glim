package com.lovedbug.geulgwi.core.domain.admin.dto.response;

import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Builder
@Getter
public class AdminQuoteResponse {

    private Long quoteId;
    private String content;
    private Integer views;
    private Integer page;
    private LocalDateTime createdAt;

    public static AdminQuoteResponse toResponseDto(Quote quote) {
        return AdminQuoteResponse.builder()
            .quoteId(quote.getQuoteId())
            .content(quote.getContent())
            .views(quote.getViews())
            .page(quote.getPage())
            .build();
    }

}
