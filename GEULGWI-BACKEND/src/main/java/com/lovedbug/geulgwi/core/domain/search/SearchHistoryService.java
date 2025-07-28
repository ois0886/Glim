package com.lovedbug.geulgwi.core.domain.search;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class SearchHistoryService {

    public List<SearchHistoryResponseDto> getSearchPopularHistory() {
        return List.of(SearchHistoryResponseDto.toSearchDto());
    }
}
