package com.lovedbug.geulgwi.core.domain.like;

import com.lovedbug.geulgwi.core.domain.quote.dto.response.QuoteResponse;
import com.lovedbug.geulgwi.core.security.annotation.CurrentUser;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class MemberLikeQuoteController {

    private final MemberLikeQuoteService memberLikeQuoteService;

    @PostMapping("/quotes/{quoteId}")
    public ResponseEntity<Void> memberLikeQuote(
        @PathVariable Long quoteId,
        @CurrentUser AuthenticatedUser user) {

        memberLikeQuoteService.likeQuote(user.getMemberId(), quoteId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quotes/{quoteId}")
    public ResponseEntity<Void> memberUnLikeQuote(
        @PathVariable Long quoteId,
        @CurrentUser AuthenticatedUser user) {

        memberLikeQuoteService.unlikeQuote(user.getMemberId(), quoteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/quotes/me")
    public ResponseEntity<List<QuoteResponse>> getMyLikeQuotes(@CurrentUser AuthenticatedUser user) {

        List<QuoteResponse> likedQuotes = memberLikeQuoteService.getLikedQuotesByMember(user.getMemberId());
        return ResponseEntity.ok(likedQuotes);
    }

}
