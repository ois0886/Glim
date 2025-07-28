package com.lovedbug.geulgwi.core.domain.curation.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import com.lovedbug.geulgwi.core.domain.curation.dto.CurationBookDto;
import com.lovedbug.geulgwi.core.domain.curation.dto.CurationContentDto;
import com.lovedbug.geulgwi.core.domain.curation.dto.CurationItemDto;
import com.lovedbug.geulgwi.core.domain.curation.dto.CurationQuoteDto;
import com.lovedbug.geulgwi.core.domain.quote.dto.QuoteWithBookDto;
import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookDto;

public class CurationMapper {

    public static CurationContentDto toCurationContent(QuoteWithBookDto quoteWithBook) {
        return CurationContentDto.builder()
            .bookId(quoteWithBook.getBookId())
            .bookTitle(quoteWithBook.getBookTitle())
            .quoteId(quoteWithBook.getQuoteId())
            .author(quoteWithBook.getAuthor())
            .publisher(quoteWithBook.getPublisher())
            .imageName(quoteWithBook.getQuoteImageName())
            .build();
    }

    public static List<CurationContentDto> toCurationContentListFromQuotes(List<QuoteWithBookDto> quoteWithBooks) {
        return quoteWithBooks.stream()
            .map(CurationMapper::toCurationContent)
            .toList();
    }

    public static CurationContentDto toCurationContent(AladdinBookDto aladdinBook) {
        return CurationContentDto.builder()
            .bookTitle(aladdinBook.getTitle())
            .author(aladdinBook.getAuthor())
            .publisher(aladdinBook.getPublisher())
            .bookCoverUrl(aladdinBook.getCoverUrl())
            .build();
    }

    public static List<CurationContentDto> toCurationContentListFromBooks(List<AladdinBookDto> aladdinBooks) {
        return aladdinBooks.stream()
            .map(CurationMapper::toCurationContent)
            .toList();
    }

    public static CurationContentDto toCurationContent(CurationBookDto curationBook) {
        return CurationContentDto.builder()
            .bookId(curationBook.getBookId())
            .bookTitle(curationBook.getBookTitle())
            .author(curationBook.getAuthor())
            .publisher(curationBook.getPublisher())
            .bookCoverUrl(curationBook.getBookCoverUrl())
            .build();
    }

    public static List<CurationItemDto> toCurationItemDtoListFromBooks(List<CurationBookDto> curationBooks) {
        Map<Long, List<CurationBookDto>> curationBooksByItemId = curationBooks.stream()
            .collect(Collectors.groupingBy(CurationBookDto::getCurationItemId));

        return curationBooksByItemId.entrySet().stream()
            .map(curationItemGroup -> {
                Long curationItemId = curationItemGroup.getKey();
                List<CurationBookDto> curationItems = curationItemGroup.getValue();

                CurationBookDto representativeBook = curationItems.get(0);

                List<CurationContentDto> contents = curationItems.stream()
                    .map(CurationMapper::toCurationContent)
                    .toList();

                return new CurationItemDto(
                    curationItemId,
                    representativeBook.getTitle(),
                    representativeBook.getDescription(),
                    CurationType.BOOK,
                    contents
                );
            })
            .toList();
    }

    public static CurationContentDto toCurationContent(CurationQuoteDto curationQuote) {
        return CurationContentDto.builder()
            .bookId(curationQuote.getBookId())
            .bookTitle(curationQuote.getBookTitle())
            .quoteId(curationQuote.getQuoteId())
            .author(curationQuote.getAuthor())
            .publisher(curationQuote.getPublisher())
            .imageName(curationQuote.getQuoteImageName())
            .build();
    }

    public static List<CurationItemDto> toCurationItemDtoListFromQuotes(List<CurationQuoteDto> curationQuotes) {
        Map<Long, List<CurationQuoteDto>> curationQuotesByItemId = curationQuotes.stream()
            .collect(Collectors.groupingBy(CurationQuoteDto::getCurationItemId));

        return curationQuotesByItemId.entrySet().stream()
            .map(entry -> {
                Long curationItemId = entry.getKey();
                List<CurationQuoteDto> curationItems = entry.getValue();

                CurationQuoteDto representativeQuote = curationItems.get(0);

                List<CurationContentDto> contents = curationItems.stream()
                    .map(CurationMapper::toCurationContent)
                    .toList();

                return new CurationItemDto(
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
