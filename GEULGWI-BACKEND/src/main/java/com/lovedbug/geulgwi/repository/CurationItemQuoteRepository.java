package com.lovedbug.geulgwi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.lovedbug.geulgwi.entity.CurationItemQuote;

@Repository
public interface CurationItemQuoteRepository extends JpaRepository<CurationItemQuote,Long> {
}
