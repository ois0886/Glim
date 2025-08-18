package com.lovedbug.geulgwi.core.domain.admin.curation;


import com.lovedbug.geulgwi.core.domain.admin.dto.request.CreateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.request.UpdateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.CreateCurationResponse;
import com.lovedbug.geulgwi.core.domain.admin.exception.CurationListEmptyException;
import com.lovedbug.geulgwi.core.domain.admin.exception.CurationNotFoundException;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
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
public class AdminCurationService {

    private static final long MAIN_CURATION_ID = 1L;

    private final MainCurationRepository mainCurationRepository;
    private final CurationItemRepository curationItemRepository;
    private final CurationItemBookRepository curationItemBookRepository;
    private final CurationItemQuoteRepository curationItemQuoteRepository;

    public List<CurationItemResponse> getMainCurationByAdmin() {

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

        CurationItem item = createCurationItem(createCurationRequest);

        switch (createCurationRequest.getCurationType()) {
            case BOOK -> handleBookItems(item, createCurationRequest.getBookIds());
            case QUOTE -> handleQuoteItems(item, createCurationRequest.getQuoteIds());
        }

        return CreateCurationResponse.builder()
                .mainCurationId(MAIN_CURATION_ID)
                .build();
    }

    @Transactional
    public void updateCurationItem(Long itemId, UpdateCurationRequest updateCurationRequest) {
        CurationItem curationItem = curationItemRepository.findById(itemId)
                .orElseThrow(() -> new CurationNotFoundException("아이템이 없습니다 itemId : " + itemId));
        curationItem.updateCurationItem(
            updateCurationRequest.getName(),
            updateCurationRequest.getDescription(),
            updateCurationRequest.getCurationType()
        );
        curationItemRepository.save(curationItem);

        curationItemBookRepository.deleteByCurationItemId(itemId);
        curationItemQuoteRepository.deleteByCurationItemId(itemId);

        switch (updateCurationRequest.getCurationType()) {
            case BOOK -> handleBookItems(curationItem, updateCurationRequest.getBookIds());
            case QUOTE -> handleQuoteItems(curationItem, updateCurationRequest.getQuoteIds());
        }
    }

    @Transactional
    public void deleteCurationItem(Long itemId) {
        if (!curationItemRepository.existsById(itemId)) {
            throw new CurationNotFoundException("아이템이 없습니다: " + itemId);
        }
        curationItemBookRepository.deleteByCurationItemId(itemId);
        curationItemQuoteRepository.deleteByCurationItemId(itemId);
        curationItemRepository.deleteById(itemId);
    }

    private CurationItem createCurationItem(CreateCurationRequest createCurationRequest) {

        CurationItem curationItem = CurationItem.builder()
                .mainCurationId(MAIN_CURATION_ID)
                .title(createCurationRequest.getName())
                .description(createCurationRequest.getDescription())
                .curationType(createCurationRequest.getCurationType())
                .build();
        return curationItemRepository.save(curationItem);
    }

    private void handleBookItems(CurationItem item, List<Long> bookIds) {

        validateIds(bookIds, item.getCurationType());
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

        validateIds(quoteIds, item.getCurationType());
        long itemId = item.getCurationItemId();
        for (Long quoteId : quoteIds) {
            CurationItemQuote curationItemQuote = CurationItemQuote.builder()
                    .curationItemId(itemId)
                    .quoteId(quoteId)
                    .build();
            curationItemQuoteRepository.save(curationItemQuote);
        }
    }

    private void validateIds(List<Long> ids, CurationType curationType) {

        if (ids == null || ids.isEmpty()) {
            throw new CurationListEmptyException(curationType + "가 비어 있습니다.");
        }
    }

}
