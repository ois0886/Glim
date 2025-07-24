package com.lovedbug.geulgwi.service;

import com.lovedbug.geulgwi.dto.resposne.SearchHistoryResponseDto;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class SearchHistoryService {

    public List<SearchHistoryResponseDto> getSearchPopularHistory() {
        return List.of(SearchHistoryResponseDto.toSearchDto());
    }
}
