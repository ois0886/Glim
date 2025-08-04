package com.lovedbug.geulgwi.core.domain.curation.repository;

import com.lovedbug.geulgwi.core.domain.curation.entity.CurationItemBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CurationItemBookRepository extends JpaRepository<CurationItemBook,Long> {
    @Modifying
    @Query("DELETE FROM CurationItemBook b WHERE b.curationItemId = :itemId")
    void deleteByCurationItemId(@Param("itemId") Long itemId);
}
