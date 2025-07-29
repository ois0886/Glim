package com.lovedbug.geulgwi.core.domain.curation.repository;

import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurationItemBookRepository extends JpaRepository<CurationItemBook,Long> {
}
