package com.lovedbug.geulgwi.core.domain.admin.quote;


import com.lovedbug.geulgwi.core.domain.admin.dto.response.AdminQuoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping("/api/v1/admin/quotes")
@RestController
@RequiredArgsConstructor
public class AdminQuoteController {

    private final AdminQuoteService adminQuoteService;

    @GetMapping
    public ResponseEntity<List<AdminQuoteResponse>> getAllQuotes() {
        return ResponseEntity.ok(adminQuoteService.getAllQuotes());
    }

    @DeleteMapping("/{quoteId}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long quoteId) {
        adminQuoteService.deleteQuote(quoteId);
        return ResponseEntity.noContent().build();
    }
}
