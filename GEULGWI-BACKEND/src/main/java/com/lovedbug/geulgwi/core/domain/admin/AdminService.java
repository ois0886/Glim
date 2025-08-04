package com.lovedbug.geulgwi.core.domain.admin;


import com.lovedbug.geulgwi.core.domain.admin.dto.request.CreateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.CreateCurationResponse;
import com.lovedbug.geulgwi.core.domain.admin.exception.CurationListEmptyException;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItem;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemBook;
import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemQuote;
import com.lovedbug.geulgwi.core.domain.curation.entity.MainCuration;
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
    public CreateCurationResponse createCuration(CreateCurationRequest req) {

        MainCuration main = createMainCuration();

        CurationItem item = createCurationItem(main, req);

        switch (req.getCurationType()) {
            case BOOK -> handleBookItems(item, req.getBookIds());
            case QUOTE -> handleQuoteItems(item, req.getQuoteIds());
            default -> throw new IllegalArgumentException("알 수 없는 CurationType: " + req.getCurationType());
        }

        return CreateCurationResponse.builder()
                .mainCurationId(main.getMainCurationId())
                .build();
    }

    private MainCuration createMainCuration() {
        return mainCurationRepository.save(
                MainCuration.builder().build()
        );
    }

    private CurationItem createCurationItem(MainCuration main, CreateCurationRequest req) {
        CurationItem toSave = CurationItem.builder()
                .mainCurationId(main.getMainCurationId())
                .title(req.getName())
                .description(req.getDescription())
                .curationType(req.getCurationType())
                .build();
        return curationItemRepository.save(toSave);
    }

    private void handleBookItems(CurationItem item, List<Long> bookIds) {
        validateIds(bookIds, "bookIds");
        long itemId = item.getCurationItemId();
        for (Long bookId : bookIds) {
            CurationItemBook bib = CurationItemBook.builder()
                    .curationItemId(itemId)
                    .bookId(bookId)
                    .build();
            curationItemBookRepository.save(bib);
        }
    }

    private void handleQuoteItems(CurationItem item, List<Long> quoteIds) {
        validateIds(quoteIds, "quoteIds");
        long itemId = item.getCurationItemId();
        for (Long quoteId : quoteIds) {
            CurationItemQuote qiq = CurationItemQuote.builder()
                    .curationItemId(itemId)
                    .quoteId(quoteId)
                    .build();
            curationItemQuoteRepository.save(qiq);
        }
    }

    private void validateIds(List<Long> ids, String fieldName) {
        if (ids == null || ids.isEmpty()) {
            throw new CurationListEmptyException(fieldName + "가 비어 있습니다.");
        }
    }

}
