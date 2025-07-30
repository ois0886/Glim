package com.lovedbug.geulgwi.core.domain.quote;

import com.lovedbug.geulgwi.core.security.annotation.CurrentUser;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.lovedbug.geulgwi.core.domain.quote.dto.request.QuoteCreateRequest;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteResponse;
import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteWithBookResponse;

@RequestMapping("/api/v1/quotes")
@RestController
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("")
    public ResponseEntity<List<QuoteWithBookResponse>> getQuotes(
        @PageableDefault(size = 10, sort = "views", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(quoteService.getQuotesByRandom(pageable));
    }

    @GetMapping("/{isbn}")
    public ResponseEntity<List<QuoteResponse>> getQuotesByIsbn(@PathVariable String isbn) {

        return ResponseEntity.ok(quoteService.getPublicQuotesByIsbn(isbn));
    }

    @PostMapping("")
    public ResponseEntity<Void> createQuote(@CurrentUser AuthenticatedUser user,
                                            @RequestPart QuoteCreateRequest quoteData, @RequestPart MultipartFile quoteImage) {

        quoteData.setMemberId(user.getMemberId());

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
