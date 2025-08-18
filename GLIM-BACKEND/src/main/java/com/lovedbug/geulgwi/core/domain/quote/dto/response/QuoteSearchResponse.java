package com.lovedbug.geulgwi.core.domain.quote.dto.response;

import lombok.Builder;
import java.util.List;

@Builder
public record QuoteSearchResponse (
    int currentPage,
    int totalPages,
    long totalResults,
    List<QuoteSearchContentResponse> contents
){
}
