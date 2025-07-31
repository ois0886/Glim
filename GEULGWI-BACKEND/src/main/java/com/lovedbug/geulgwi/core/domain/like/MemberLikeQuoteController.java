package com.lovedbug.geulgwi.core.domain.like;

import com.lovedbug.geulgwi.core.domain.like.dto.request.MemberLikeQuoteRequest;
import com.lovedbug.geulgwi.core.security.annotation.CurrentUser;
import com.lovedbug.geulgwi.core.security.dto.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class MemberLikeQuoteController {

    private final MemberLikeQuoteService memberLikeQuoteService;

    @PostMapping("/quotes")
    public ResponseEntity<Void> memberLikeQuote(
        @RequestBody MemberLikeQuoteRequest likeQuoteRequest,
        @CurrentUser AuthenticatedUser user) {

        memberLikeQuoteService.likeQuote(user.getMemberId(), likeQuoteRequest.getQuoteId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/quotes")
    public ResponseEntity<Void> memberUnLikeQuote(
        @RequestBody MemberLikeQuoteRequest likeQuoteRequest,
        @CurrentUser AuthenticatedUser user) {

        memberLikeQuoteService.unlikeQuote(user.getMemberId(), likeQuoteRequest.getQuoteId());
        return ResponseEntity.ok().build();
    }

}
