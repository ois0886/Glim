package com.lovedbug.geulgwi.core.domain.like.repository;

import com.lovedbug.geulgwi.core.domain.like.entity.MemberLikeQuote;
import feign.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MemberLikeQuoteRepository extends JpaRepository<MemberLikeQuote, Long> {

    boolean existsByMemberIdAndQuote_QuoteId(Long memberId, Long quoteId);

    void deleteByMemberIdAndQuote_QuoteId(Long memberId, Long quoteId);

    long countByQuote_QuoteId(Long quoteId);

    @Query("SELECT q.quoteId, COUNT(mlq) FROM MemberLikeQuote mlq JOIN mlq.quote q " +
        "WHERE q.quoteId IN :quoteIds GROUP BY q.quoteId")
    List<Object[]> countByQuoteIds(@Param("quoteIds")List<Long> quoteIds);

    @EntityGraph(attributePaths = {"quote"})
    List<MemberLikeQuote> findAllByMemberId(Long memberId);
}
