package com.lovedbug.geulgwi.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lovedbug.geulgwi.constant.AladdinListQueryType;
import com.lovedbug.geulgwi.constant.AladdinSearchQueryType;
import com.lovedbug.geulgwi.dto.resposne.AladdinBookDto;
import com.lovedbug.geulgwi.service.BookService;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("")
    public ResponseEntity<List<AladdinBookDto>> getBooksByKeyword(
        @RequestParam(name = "keyword") String keyword,
        @RequestParam(name = "page", required = false, defaultValue = "1") int page,
        @RequestParam(defaultValue = "KEYWORD") AladdinSearchQueryType searchQueryType) {

        return ResponseEntity
            .ok()
            .body(bookService.getBooksByKeyword(searchQueryType, keyword, page));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<AladdinBookDto>> getBestSellerBooks(
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

