package com.lovedbug.geulgwi.core.domain.like.repository;

import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeQuoteRepository extends JpaRepository<MemberLikeQuote, Long> {

    boolean existsByMemberIdAndQuote_QuoteId(Long memberId, Long quoteId);
    void deleteByMemberIdAndQuote_QuoteId(Long memberId, Long quoteId);
    long countByQuote_QuoteId(Long quoteId);
}
