package com.lovedbug.geulgwi.core.domain.like.repository;

import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberLikeQuoteRepository extends JpaRepository<MemberLikeQuote, Long> {

    boolean existsByMemberIdAndQuoteId(Long memberId, Long quoteId);
    void deleteByMemberIdAndQuoteId(Long memberId, Long quoteId);
    long countByQuoteId(Long quoteId);
}
