package com.lovedbug.geulgwi.core.domain.quote.mapper;

import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteSearchContentResponse;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteSearchResponse;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteWithBookResponse;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import org.springframework.data.domain.Page;
import java.util.List;

public class QuoteMapper {

    public static QuoteSearchResponse toQuoteSearchResponse(Page<Quote> quotes, Long memberId) {
        List<QuoteSearchContentResponse> contents = quotes.getContent().stream()
            .map(quote -> {
                boolean isLiked = quote.getLikes().stream()
                    .anyMatch(like -> like.getMemberId().equals(memberId));

                return QuoteSearchContentResponse.builder()
                    .quoteId(quote.getQuoteId())
                    .bookTitle(quote.getBook().getTitle())
                    .content(quote.getContent())
                    .page(quote.getPage())
                    .views(quote.getViews())
                    .likes(quote.getLikes().size())
                    .isliked(isLiked)
                    .build();
            })
            .toList();

        return QuoteSearchResponse.builder()
            .currentPage(quotes.getNumber())
            .totalPages(quotes.getTotalPages())
            .totalResults(quotes.getTotalElements())
            .contents(contents)
            .build();
    }

    public static QuoteWithBookResponse toQuoteWithBookResponse(Quote quote) {
        return QuoteWithBookResponse.builder()
            .quoteId(quote.getQuoteId())
            .quoteImageName(quote.getImageName())
            .page(quote.getPage())
            .bookId(quote.getBook().getBookId())
            .bookTitle(quote.getBook().getTitle())
            .author(quote.getBook().getAuthor())
            .publisher(quote.getBook().getPublisher())
            .bookCoverUrl(quote.getBook().getCoverUrl())
            .build();
    }

    public static QuoteWithBookResponse toQuoteWithBookResponse(Quote quote, Long memberId) {
        return QuoteWithBookResponse.builder()
            .quoteId(quote.getQuoteId())
            .quoteImageName(quote.getImageName())
            .quoteViews(quote.getViews())
            .page(quote.getPage())
            .bookId(quote.getBook().getBookId())
            .bookTitle(quote.getBook().getTitle())
            .author(quote.getBook().getAuthor())
            .publisher(quote.getBook().getPublisher())
            .bookCoverUrl(quote.getBook().getCoverUrl())
            .isLiked(quote.getLikes().stream()
                .anyMatch(like -> like.getMemberId().equals(memberId)))
            .likeCount(quote.getLikes().size())
            .build();
    }
}
