package com.lovedbug.geulgwi.repository;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.lovedbug.geulgwi.entity.Quote;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {
    
    @EntityGraph(attributePaths = {"book"})
    @Query("SELECT q FROM Quote q WHERE q.visibility = 'PUBLIC'")
    List<Quote> findPublicQuotes(Pageable pageable);
}
