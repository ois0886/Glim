package com.lovedbug.geulgwi.core.domain.curation.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.lovedbug.geulgwi.core.domain.book.dto.PopularBookResponse;
import com.lovedbug.geulgwi.core.domain.book.entity.Book;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import com.lovedbug.geulgwi.core.domain.curation.projection.CurationBook;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationContentResponse;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.projection.CurationQuote;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteWithBookResponse;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookResponse;

public class CurationMapper {

    public static CurationContentResponse toCurationContent(QuoteWithBookResponse quoteWithBook) {
        return CurationContentResponse.builder()
            .bookId(quoteWithBook.getBookId())
            .bookTitle(quoteWithBook.getBookTitle())
            .quoteId(quoteWithBook.getQuoteId())
            .author(quoteWithBook.getAuthor())
            .publisher(quoteWithBook.getPublisher())
            .imageName(quoteWithBook.getQuoteImageName())
            .build();
    }

    public static List<CurationContentResponse> toCurationContentListFromQuotes(List<QuoteWithBookResponse> quoteWithBooks) {
        return quoteWithBooks.stream()
            .map(CurationMapper::toCurationContent)
            .toList();
    }

    public static CurationContentResponse toCurationContent(AladdinBookResponse aladdinBook) {
        return CurationContentResponse.builder()
            .bookTitle(aladdinBook.getTitle())
            .author(aladdinBook.getAuthor())
            .publisher(aladdinBook.getPublisher())
            .bookCoverUrl(aladdinBook.getCoverUrl())
            .build();
    }

    public static CurationContentResponse toCurationContent(PopularBookResponse popularBook) {
        return CurationContentResponse.builder()
            .bookId(popularBook.bookId())
            .bookTitle(popularBook.bookTitle())
            .author(popularBook.author())
            .publisher(popularBook.publisher())
            .bookCoverUrl(popularBook.bookCoverUrl())
            .build();
    }

    public static List<CurationContentResponse> toCurationContentListFromBooks(List<PopularBookResponse> popularBooks) {
        return popularBooks.stream()
            .map(CurationMapper::toCurationContent)
            .toList();
    }

    public static CurationContentResponse toCurationContent(CurationBook curationBook) {
        return CurationContentResponse.builder()
            .bookId(curationBook.getBookId())
            .bookTitle(curationBook.getBookTitle())
            .author(curationBook.getAuthor())
            .publisher(curationBook.getPublisher())
            .bookCoverUrl(curationBook.getBookCoverUrl())
            .build();
    }

    public static List<CurationItemResponse> toCurationItemDtoListFromBooks(List<CurationBook> curationBooks) {
        Map<Long, List<CurationBook>> curationBooksByItemId = curationBooks.stream()
            .collect(Collectors.groupingBy(CurationBook::getCurationItemId));

        return curationBooksByItemId.entrySet().stream()
            .map(curationItemGroup -> {
                Long curationItemId = curationItemGroup.getKey();
                List<CurationBook> curationItems = curationItemGroup.getValue();

                CurationBook representativeBook = curationItems.get(0);

                List<CurationContentResponse> contents = curationItems.stream()
                    .map(CurationMapper::toCurationContent)
                    .toList();

                return new CurationItemResponse(
                    curationItemId,
                    representativeBook.getTitle(),
                    representativeBook.getDescription(),
                    CurationType.BOOK,
                    contents
                );
            })
            .toList();
    }

    public static CurationContentResponse toCurationContent(CurationQuote curationQuote) {
        return CurationContentResponse.builder()
            .bookId(curationQuote.getBookId())
            .bookTitle(curationQuote.getBookTitle())
            .quoteId(curationQuote.getQuoteId())
            .author(curationQuote.getAuthor())
            .publisher(curationQuote.getPublisher())
            .imageName(curationQuote.getQuoteImageName())
            .build();
    }

    public static List<CurationItemResponse> toCurationItemDtoListFromQuotes(List<CurationQuote> curationQuotes) {
        Map<Long, List<CurationQuote>> curationQuotesByItemId = curationQuotes.stream()
            .collect(Collectors.groupingBy(CurationQuote::getCurationItemId));

        return curationQuotesByItemId.entrySet().stream()
            .map(entry -> {
                Long curationItemId = entry.getKey();
                List<CurationQuote> curationItems = entry.getValue();

                CurationQuote representativeQuote = curationItems.get(0);

                List<CurationContentResponse> contents = curationItems.stream()
                    .map(CurationMapper::toCurationContent)
                    .toList();

                return new CurationItemResponse(
                    curationItemId,
                    representativeQuote.getTitle(),
                    representativeQuote.getDescription(),
                    CurationType.QUOTE,
                    contents
                );
            })
            .toList();
    }
}
