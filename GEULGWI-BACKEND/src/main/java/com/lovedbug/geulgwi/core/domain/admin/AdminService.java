package com.lovedbug.geulgwi.core.domain.admin;


import com.lovedbug.geulgwi.core.domain.admin.dto.request.CreateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.CreateCurationResponse;
import com.lovedbug.geulgwi.core.domain.admin.exception.CurationListEmptyException;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItem;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemBook;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemQuote;
import com.lovedbug.geulgwi.core.domain.curation.mapper.CurationMapper;
import com.lovedbug.geulgwi.core.domain.curation.repository.CurationItemBookRepository;
import com.lovedbug.geulgwi.core.domain.curation.repository.CurationItemQuoteRepository;
import com.lovedbug.geulgwi.core.domain.curation.repository.CurationItemRepository;
import com.lovedbug.geulgwi.core.domain.curation.repository.MainCurationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MainCurationRepository mainCurationRepository;
    private final CurationItemRepository curationItemRepository;
    private final CurationItemBookRepository curationItemBookRepository;
    private final CurationItemQuoteRepository curationItemQuoteRepository;

    public List<CurationItemResponse> getMainCurationByAdmin() {
        final long MAIN_CURATION_ID = 1L;

        return Stream.of(
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

    @Transactional
    public CreateCurationResponse createCuration(CreateCurationRequest createCurationRequest) {

        final long MAIN_CURATION_ID = 1L;

        CurationItem item = createCurationItem(createCurationRequest);

        switch (createCurationRequest.getCurationType()) {
            case BOOK -> handleBookItems(item, createCurationRequest.getBookIds());
            case QUOTE -> handleQuoteItems(item, createCurationRequest.getQuoteIds());
            default -> throw new IllegalArgumentException("알 수 없는 CurationType: " + createCurationRequest.getCurationType());
        }

        return CreateCurationResponse.builder()
                .mainCurationId(MAIN_CURATION_ID)
                .build();
    }

    private CurationItem createCurationItem(CreateCurationRequest createCurationRequest) {
        final long MAIN_CURATION_ID = 1L;
         CurationItem curationItem = CurationItem.builder()
                .mainCurationId(MAIN_CURATION_ID)
                .title(createCurationRequest.getName())
                .description(createCurationRequest.getDescription())
                .curationType(createCurationRequest.getCurationType())
                .build();
        return curationItemRepository.save(curationItem);
    }

    private void handleBookItems(CurationItem item, List<Long> bookIds) {
        validateIds(bookIds, "bookIds");
        long itemId = item.getCurationItemId();
        for (Long bookId : bookIds) {
            CurationItemBook curationItemBook = CurationItemBook.builder()
                    .curationItemId(itemId)
                    .bookId(bookId)
                    .build();
            curationItemBookRepository.save(curationItemBook);
        }
    }

    private void handleQuoteItems(CurationItem item, List<Long> quoteIds) {
        validateIds(quoteIds, "quoteIds");
        long itemId = item.getCurationItemId();
        for (Long quoteId : quoteIds) {
            CurationItemQuote curationItemQuote = CurationItemQuote.builder()
                    .curationItemId(itemId)
                    .quoteId(quoteId)
                    .build();
            curationItemQuoteRepository.save(curationItemQuote);
        }
    }

    private void validateIds(List<Long> ids, String fieldName) {
        if (ids == null || ids.isEmpty()) {
            throw new CurationListEmptyException(fieldName + "가 비어 있습니다.");
        }
    }

}
