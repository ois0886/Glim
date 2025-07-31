package com.lovedbug.geulgwi.core.domain.admin;


import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.mapper.CurationMapper;
import com.lovedbug.geulgwi.core.domain.curation.repository.MainCurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MainCurationRepository mainCurationRepository;

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
}
