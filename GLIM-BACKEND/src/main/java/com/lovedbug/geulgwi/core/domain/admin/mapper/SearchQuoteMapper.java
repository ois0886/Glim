package com.lovedbug.geulgwi.core.domain.admin.mapper;

import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchQuoteResponse;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import java.util.List;
import java.util.stream.Collectors;

public final class SearchQuoteMapper {
    public static SearchQuoteResponse toSearchQuoteResponse(Quote quote) {

        return new SearchQuoteResponse(
            quote.getQuoteId(),
            quote.getContent(),
            quote.getViews(),
            quote.getPage(),
            quote.getImageName(),
            quote.getBookTitle()
        );
    }

    public static List<SearchQuoteResponse> toDtoList(List<Quote> quotes) {
        return quotes.stream()
            .map(SearchQuoteMapper::toSearchQuoteResponse)
            .collect(Collectors.toList());
    }
}
