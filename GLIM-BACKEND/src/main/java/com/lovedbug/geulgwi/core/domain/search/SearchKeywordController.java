package com.lovedbug.geulgwi.core.domain.search;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/search-keywords")
@RequiredArgsConstructor
public class SearchKeywordController {

    private final SearchKeywordService searchKeywordService;

    @GetMapping("/popular")
    public ResponseEntity<List<String>> getSearchHistory() {

        return ResponseEntity
            .ok()
            .body(searchKeywordService.getPopularSearchKeywords());
    }
}
