package com.lovedbug.geulgwi.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.lovedbug.geulgwi.dto.request.QuoteCreateDto;
import com.lovedbug.geulgwi.dto.resposne.QuoteWithBookDto;
import com.lovedbug.geulgwi.service.QuoteService;

@RequestMapping("/api/v1/quotes")
@RestController
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("")
    public ResponseEntity<List<QuoteWithBookDto>> getQuotes(
        @PageableDefault(size = 10, sort = "views", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(quoteService.getQuotes(pageable));
    }

    @PostMapping("")
    public ResponseEntity<Void> createQuote(
        @RequestPart QuoteCreateDto quoteData, @RequestPart MultipartFile quoteImage) {

        quoteService.createQuote(quoteData, quoteImage);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/views")
    public ResponseEntity<Void> increaseViewCount(@PathVariable(value = "id") long quoteId) {

        quoteService.increaseViewCount(quoteId);

        return ResponseEntity
            .noContent()
            .build();
    }
}
