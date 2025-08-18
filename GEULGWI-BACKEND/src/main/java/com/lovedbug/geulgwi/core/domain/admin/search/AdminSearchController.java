package com.lovedbug.geulgwi.core.domain.admin.search;


import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchBookResponse;
import com.lovedbug.geulgwi.core.domain.admin.dto.response.SearchQuoteResponse;
import com.lovedbug.geulgwi.core.domain.member.dto.response.MemberResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/search-keywords")
@RequiredArgsConstructor
public class AdminSearchController {

    private final AdminSearchService adminSearchService;

    @GetMapping("/books")
    public ResponseEntity<List<SearchBookResponse>> searchBooks(
        @RequestParam("keyword") String keyword,
        @PageableDefault(size = 10, sort = "views", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok().body(
            adminSearchService.searchBooks(keyword,pageable)
        );
    }

    @GetMapping("/quotes")
    public ResponseEntity<List<SearchQuoteResponse>> searchQuotes(
        @RequestParam("keyword") String keyword,
        @PageableDefault(size = 10, sort = "views", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok().body(
            adminSearchService.searchQuotes(keyword,pageable)
        );
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> searchMembers(
        @RequestParam("keyword") String keyword,
        @PageableDefault(size = 10, sort = "views", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        return ResponseEntity.ok().body(
            adminSearchService.searchMembers(keyword,pageable)
        );
    }
}
