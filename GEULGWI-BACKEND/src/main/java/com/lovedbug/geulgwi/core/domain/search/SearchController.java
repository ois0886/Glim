package com.lovedbug.geulgwi.core.domain.search;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/searches")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/popular")
    public ResponseEntity<List<SearchHistoryResponseDto>> getSearchHistory() {

        return ResponseEntity
            .ok()
            .body(searchService.getSearchPopularHistory());
    }
}
