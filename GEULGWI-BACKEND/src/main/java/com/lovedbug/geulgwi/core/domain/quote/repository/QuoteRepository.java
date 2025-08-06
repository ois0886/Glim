package com.lovedbug.geulgwi.core.domain.quote.repository;

import java.util.List;
import java.util.Optional;
import com.lovedbug.geulgwi.core.domain.quote.entity.Quote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @EntityGraph(attributePaths = {"book"})
    @Query("SELECT q FROM Quote q WHERE q.visibility = 'PUBLIC'")
    List<Quote> findPublicQuotesWithBook(Pageable pageable);

    @Query("SELECT q FROM Quote q WHERE q.visibility = 'PUBLIC'")
    List<Quote> findPublicQuotes(Pageable pageable);

    @EntityGraph(attributePaths = {"book"})
    List<Quote> findAllByBookIsbnAndVisibility(String isbn, String visibility);

    @EntityGraph(attributePaths = {"book", "likes"})
    Page<Quote> findByContentContainingAndVisibility(String content, String visibility, Pageable pageable);

    @Query("SELECT q FROM Quote q JOIN FETCH q.book WHERE q.visibility = 'PUBLIC' ORDER BY function('random')")
    List<Quote> findPublicQuotesByRandom(Pageable pageable);

    @EntityGraph(attributePaths = {"book", "likes"})
    Optional<Quote> findByQuoteIdAndVisibility(Long quoteId, String visibility);
}
