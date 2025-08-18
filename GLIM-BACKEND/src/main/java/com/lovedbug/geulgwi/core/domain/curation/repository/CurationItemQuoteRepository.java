package com.lovedbug.geulgwi.core.domain.curation.repository;

import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CurationItemQuoteRepository extends JpaRepository<CurationItemQuote,Long> {
    @Modifying
    @Query("DELETE FROM CurationItemQuote q WHERE q.curationItemId = :itemId")
    void deleteByCurationItemId(@Param("itemId") Long itemId);
}
