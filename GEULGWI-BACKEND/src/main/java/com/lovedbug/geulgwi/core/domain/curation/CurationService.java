package com.lovedbug.geulgwi.core.domain.curation;

import com.lovedbug.geulgwi.core.domain.book.BookService;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import com.lovedbug.geulgwi.core.domain.curation.repository.MainCurationRepository;
import com.lovedbug.geulgwi.core.domain.quote.QuoteService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationContentResponse;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.mapper.CurationMapper;

@Service
@RequiredArgsConstructor
public class CurationService {

    private final BookService bookService;
    private final QuoteService quoteService;
    private final MainCurationRepository mainCurationRepository;

    public List<CurationItemResponse> getMainCuration() {
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

    public List<CurationItemResponse> getBookCurationsById(long curationId) {
        return CurationMapper.toCurationItemDtoListFromBooks(
            mainCurationRepository.findCurationBooksByCurationId(curationId)
        );
    }

    public List<CurationItemResponse> getQuoteCurationsById(long curationId) {
        return CurationMapper.toCurationItemDtoListFromQuotes(
            mainCurationRepository.findCurationQuotesByCurationId(curationId)
        );
    }

    public CurationItemResponse getPopularQuoteCuration() {
        List<CurationContentResponse> contents = CurationMapper.toCurationContentListFromQuotes(
            quoteService.getPopularQuotesWithBook()
        );

        return CurationItemResponse.builder()
            .title("현재 인기 많은 글귀")
            .description("현재 인기 많은 글귀 입니다")
            .curationType(CurationType.QUOTE)
            .contents(contents)
            .build();
    }

    public CurationItemResponse getPopularBookCuration() {
        List<CurationContentResponse> contents = CurationMapper.toCurationContentListFromBooks(
            bookService.getPopularBooks());

        return CurationItemResponse.builder()
            .title("현재 인기 많은 도서")
            .description("현재 인기 많은 도서 입니다")
            .curationType(CurationType.BOOK)
            .contents(contents)
            .build();
    }
}

