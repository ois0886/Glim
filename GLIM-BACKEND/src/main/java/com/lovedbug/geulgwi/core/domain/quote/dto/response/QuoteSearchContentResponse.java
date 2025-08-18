package com.lovedbug.geulgwi.core.domain.quote.dto.response;

import lombok.Builder;

@Builder
public record QuoteSearchContentResponse (
    Long quoteId,
    String bookTitle,
    String content,
    int views,
    int page,
    int likes,
    boolean isliked
){
}
