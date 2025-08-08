package com.lovedbug.geulgwi.core.domain.curation;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.lovedbug.geulgwi.core.domain.curation.dto.response.CurationItemResponse;
import com.lovedbug.geulgwi.core.domain.curation.mapper.CurationMapper;
import com.lovedbug.geulgwi.core.domain.curation.repository.MainCurationRepository;

@RequiredArgsConstructor
@Service
public class CurationCacheService {

    private final MainCurationRepository mainCurationRepository;

    @Cacheable(value = "bookCurations", key = "#curationId")
    public List<CurationItemResponse> getBookCurationsById(long curationId) {
        return CurationMapper.toCurationItemDtoListFromBooks(
            mainCurationRepository.findCurationBooksByCurationId(curationId)
        );
    }

    @Cacheable(value = "quoteCurations", key = "#curationId")
    public List<CurationItemResponse> getQuoteCurationsById(long curationId) {
        return CurationMapper.toCurationItemDtoListFromQuotes(
            mainCurationRepository.findCurationQuotesByCurationId(curationId)
        );
    }
}
