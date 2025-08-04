package com.lovedbug.geulgwi.core.domain.admin;


import com.lovedbug.geulgwi.core.domain.admin.dto.request.CreateCurationRequest;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.CreateCurationResponse;
import com.lovedbug.geulgwi.core.domain.admin.exception.CurationListEmptyException;
import com.lovedbug.geulgwi.core.domain.curation.constant.CurationType;
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

    public CreateCurationResponse createCuration(CreateCurationRequest createCurationRequest) {
        MainCuration mainCuration = mainCurationRepository.save(
                MainCuration.builder().build()
        );
        CurationItem item = CurationItem.builder()
                .mainCurationId(mainCuration.getMainCurationId())
                .title(createCurationRequest.getName())
                .description(createCurationRequest.getDescription())
                .curationType(createCurationRequest.getCurationType())
                .build();
        curationItemRepository.save(item);
        if(createCurationRequest.getIds() != null){
            createCurationRequest.getIds().forEach(id -> {
                 if(createCurationRequest.getCurationType() == CurationType.BOOK){
                     CurationItemBook curationItemBook = CurationItemBook.builder()
                             .curationItemId(item.getCurationItemId())
                             .bookId(id)
                             .build();
                     curationItemBookRepository.save(curationItemBook);
                 }
                 else{
                     CurationItemQuote curationItemQuote = CurationItemQuote.builder()
                             .curationItemId(item.getCurationItemId())
                             .quoteId(id)
                             .build();
                     curationItemQuoteRepository.save(curationItemQuote);
                 }
            });
        }
        else{
            throw new CurationListEmptyException("curation id list가 empty입니다.");
        }

        return CreateCurationResponse.builder()
                .mainCurationId(mainCuration.getMainCurationId())
                .build();
    }
}
