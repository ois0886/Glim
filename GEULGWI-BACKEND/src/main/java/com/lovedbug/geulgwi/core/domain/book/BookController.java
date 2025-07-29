package com.lovedbug.geulgwi.core.domain.book;

import com.lovedbug.geulgwi.external.book_provider.aladdin.dto.AladdinBookResponse;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.external.book_provider.aladdin.constant.AladdinSearchQueryType;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("")
    public ResponseEntity<List<AladdinBookResponse>> getBooksByKeyword(
        @RequestParam(name = "keyword") String keyword,
        @RequestParam(name = "page", required = false, defaultValue = "1") int page,
        @RequestParam(defaultValue = "KEYWORD") AladdinSearchQueryType searchQueryType) {

        return ResponseEntity
            .ok()
            .body(bookService.getBooksByKeyword(searchQueryType, keyword, page));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<AladdinBookResponse>> getBestSellerBooks(
        @RequestParam(name = "page", required = false, defaultValue = "1") int page,
        @RequestParam(defaultValue = "BESTSELLER") AladdinListQueryType listQueryType) {

        return ResponseEntity
            .ok()
            .body(bookService.getBestSellerBooks(listQueryType, page));
    }

    @PatchMapping("/{id}/views")
    public ResponseEntity<Void> increaseViewCount(@PathVariable(value = "id") long bookId) {

        bookService.increaseViewCount(bookId);

        return ResponseEntity
            .noContent()
            .build();
    }

}

