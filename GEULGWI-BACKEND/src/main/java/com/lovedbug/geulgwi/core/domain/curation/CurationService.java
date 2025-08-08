package com.lovedbug.geulgwi.core.domain.curation;

import com.lovedbug.geulgwi.core.common.exception.GeulgwiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.stereotype.Service;
import com.lovedbug.geulgwi.core.domain.book.BookService;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationContentResponse;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.mapper.CurationMapper;
import com.lovedbug.geulgwi.core.domain.quote.QuoteService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurationService {

    private final BookService bookService;
    private final QuoteService quoteService;
    private final CurationCacheService curationCacheService;

    public List<CurationItemResponse> getMainCuration() {
        final long MAIN_CURATION_ID = 1L;

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<List<CurationItemResponse>>> futures = List.of(
                executor.submit(() -> List.of(getPopularQuoteCuration())),
                executor.submit(() -> List.of(getPopularBookCuration())),
                executor.submit(() -> curationCacheService.getBookCurationsById(MAIN_CURATION_ID)),
                executor.submit(() -> curationCacheService.getQuoteCurationsById(MAIN_CURATION_ID))
            );

            return futures.stream()
                .flatMap(future -> {
                    try {
                        return future.get().stream();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new GeulgwiException("병렬적으로 큐레이션 요소 조회 중 인터럽트 발생", e);
                    } catch (ExecutionException e) {
                        throw new GeulgwiException("메인 큐레이션 병렬 조회 중 오류 발생", e);
                    }
                })
                .toList();
        }
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

