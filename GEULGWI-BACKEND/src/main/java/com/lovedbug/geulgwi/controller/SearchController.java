package com.lovedbug.geulgwi.controller;

import com.lovedbug.geulgwi.dto.resposne.SearchHistoryResponseDto;
import com.lovedbug.geulgwi.service.SearchHistoryService;
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

    private final SearchHistoryService searchHistoryService;

    @GetMapping("/popular")
    public ResponseEntity<List<SearchHistoryResponseDto>> getSearchHistory() {

        return ResponseEntity
            .ok()
            .body(searchHistoryService.getSearchPopularHistory());
    }
}
