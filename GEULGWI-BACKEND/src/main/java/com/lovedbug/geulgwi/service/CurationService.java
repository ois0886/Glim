package com.lovedbug.geulgwi.service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import com.lovedbug.geulgwi.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.constant.CurationType;
import com.lovedbug.geulgwi.dto.resposne.CurationContentDto;
import com.lovedbug.geulgwi.dto.resposne.CurationItemDto;
import com.lovedbug.geulgwi.mapper.CurationMapper;
import com.lovedbug.geulgwi.repository.MainCurationRepository;

@Service
@RequiredArgsConstructor
public class CurationService {

    private final BookService bookService;
    private final QuoteService quoteService;
    private final MainCurationRepository mainCurationRepository;

    public List<CurationItemDto> getMainCuration() {
        final long MAIN_CURATION_ID = 1L;

        return Stream.of(
                List.of(
                    getPopularQuoteCuration(),
                    getPopularBookCuration()),
                getBookCurationsById(MAIN_CURATION_ID),
                getQuoteCurationsById(MAIN_CURATION_ID))
            .flatMap(List::stream)
            .toList();
    }

    public List<CurationItemDto> getBookCurationsById(long curationId) {
        return CurationMapper.toCurationItemDtoListFromBooks(
            mainCurationRepository.findCurationBooksByCurationId(curationId)
        );
    }

    public List<CurationItemDto> getQuoteCurationsById(long curationId) {
        return CurationMapper.toCurationItemDtoListFromQuotes(
            mainCurationRepository.findCurationQuotesByCurationId(curationId)
        );
    }

    public CurationItemDto getPopularQuoteCuration() {
        List<CurationContentDto> contents = CurationMapper.toCurationContentListFromQuotes(
            quoteService.getPopularQuotesWithBook()
        );

        return CurationItemDto.builder()
            .title("현재 인기 많은 글귀")
            .description("현재 인기 많은 글귀 입니다")
            .curationType(CurationType.QUOTE)
            .contents(contents)
            .build();
    }

    public CurationItemDto getPopularBookCuration() {
        List<CurationContentDto> contents = CurationMapper.toCurationContentListFromBooks(
            bookService.getBestSellerBooks(AladdinListQueryType.BESTSELLER, 10)
        );

        return CurationItemDto.builder()
            .title("현재 인기 많은 도서")
            .description("현재 인기 많은 도서 입니다")
            .curationType(CurationType.BOOK)
            .contents(contents)
            .build();
    }
}

